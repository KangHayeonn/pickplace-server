package com.server.pickplace.reservation.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.entity.QMember;
import com.server.pickplace.place.entity.QUnit;
import com.server.pickplace.place.entity.Room;
import com.server.pickplace.place.entity.Unit;
import com.server.pickplace.reservation.dto.MemberInfoResponse;
import com.server.pickplace.reservation.dto.PayInfoResponse;
import com.server.pickplace.reservation.dto.PayRequest;
import com.server.pickplace.reservation.dto.PlaceInfoResponse;
import com.server.pickplace.reservation.entity.QRPaymentInfomation;
import com.server.pickplace.reservation.entity.QRStatus;
import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.reservation.entity.ReservationStatus;
import com.server.pickplace.reservation.error.ReservationErrorResult;
import com.server.pickplace.reservation.error.ReservationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.server.pickplace.member.entity.QMember.member;
import static com.server.pickplace.place.entity.QPlace.*;
import static com.server.pickplace.place.entity.QRoom.*;
import static com.server.pickplace.place.entity.QUnit.unit;
import static com.server.pickplace.reservation.entity.QReservation.reservation;

@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public Map<String, Object> getReservationPageMapByEmailAndRoomId(String email, Long roomId) {

        Map<String, Object> reservationPageMap = new HashMap<>();

        Tuple placePayTuple = queryFactory
                .select(Projections.bean(PlaceInfoResponse.class,
                                place.name.as("placeName"),
                                room.name.as("roomName"),
                                room.peopleNum.as("roomMaxNum"),
                                place.address.as("address")),

                        Projections.bean(PayInfoResponse.class,
                                room.price))
                .from(room)
                .join(room.place, place)
                .where(room.id.eq(roomId))
                .fetchOne();

        PlaceInfoResponse placeInfoResponse = placePayTuple.get(0, PlaceInfoResponse.class);
        PayInfoResponse payInfoResponse = placePayTuple.get(1, PayInfoResponse.class);

        MemberInfoResponse memberInfoResponse = queryFactory
                .select(Projections.bean(MemberInfoResponse.class,
                        member.name,
                        member.number))
                .from(member)
                .where(member.email.eq(email))
                .fetchOne();

        reservationPageMap.put("place", placeInfoResponse);
        reservationPageMap.put("payment", payInfoResponse);
        reservationPageMap.put("member", memberInfoResponse);

        return reservationPageMap;
    }

    @Override
    public void makeReservation(String email, PayRequest payRequest) {

        // 1. 모든 호실ID 리스트
        List<Long> allUnitIdList = queryFactory
                .select(unit.id)
                .from(room)
                .join(room.units, unit).on(room.id.eq(payRequest.getRoomId()))
                .fetch();

        // 2. 시간조건 걸리는 unit 리스트
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String stringStartDateTime = payRequest.getCheckInTime().format(formatter);
        String sql = String.format("""
                    select distinct unit_id
                    from
                    		(
                            select unit_tb.unit_id, CONCAT(start_date, ' ', start_time) AS start_datetime, CONCAT(end_date, ' ', end_time) AS end_datetime
                    		from room_tb
                    		join unit_tb on (room_tb.room_id = %d) and (room_tb.room_id = unit_tb.room_id)
                    		join reservation_tb on room_tb.room_id = reservation_tb.room_id
                            ) sub_query
                    
                    where '%s' between start_datetime and end_datetime
                """, payRequest.getRoomId(), stringStartDateTime);

        List<BigInteger> unableUnitIdList = em.createNativeQuery(sql).getResultList();
        unableUnitIdList.forEach(o -> allUnitIdList.remove(o.longValue()));

        if (allUnitIdList.isEmpty()) {
            throw new ReservationException(ReservationErrorResult.NO_EMPTY_ROOM);
        }

        // 3. 예약
        Long unitId = allUnitIdList.get(0);
        Unit unit = queryFactory
                .select(QUnit.unit)
                .from(QUnit.unit)
                .where(QUnit.unit.id.eq(unitId))
                .fetchOne();
        Room room = unit.getRoom();
        Member member = getMember(email);

        // 2. 예약 생성

        Reservation reservation = Reservation.builder()
                .reservationNum(UUID.randomUUID().toString())
                .peopleNum(room.getPeopleNum())
                .status(ReservationStatus.PAYMENT)
                .startDate(payRequest.getStartDate())
                .startTime(payRequest.getStartTime())
                .endDate(payRequest.getEndDate())
                .endTime(payRequest.getEndTime())
                .member(member)
                .room(room)
                .unit(unit)
                .build();

        em.persist(reservation);

    }


    @Override
    public String saveQRPaymentInformation(String email, Integer roomPrice) {

        String uuid = UUID.randomUUID().toString();

        QRPaymentInfomation qrPaymentInfomation = QRPaymentInfomation.builder()
                .qrPaymentCode(uuid)
                .email(email)
                .status(QRStatus.WAITING)
                .price(roomPrice)
                .build();

        em.persist(qrPaymentInfomation);

        return uuid;

    }

    @Override
    public void changeQREntityStatus(QRPaymentInfomation qrPaymentInfomation, QRStatus status) {
        qrPaymentInfomation.setStatus(status);

        em.merge(qrPaymentInfomation);
    }

    @Override
    public void roomIdCheck(Long roomId) {
        Optional<Room> room1 = Optional.ofNullable(queryFactory
                .select(room)
                .from(room)
                .where(room.id.eq(roomId))
                .fetchOne());

        room1.orElseThrow(() -> new ReservationException(ReservationErrorResult.NO_EXIST_ROOM_ID));
    }

    private Member getMember(String email) {
        return queryFactory
                .select(QMember.member)
                .from(QMember.member)
                .where(QMember.member.email.eq(email))
                .fetchOne();
    }


    private BooleanExpression dateCheckByRequest(PayRequest request) {


        BooleanExpression cond1 = reservation.startDate.goe(request.getStartDate()).and(reservation.startDate.lt(request.getEndDate()));
        BooleanExpression cond2 = reservation.endDate.gt(request.getStartDate()).and(reservation.endDate.loe(request.getEndDate()));
        BooleanExpression condition = cond1.or(cond2);

        return condition;
    }

    private BooleanExpression timeCheckByRequest(PayRequest request) {


        BooleanExpression cond1 = reservation.startTime.goe(request.getStartTime()).and(reservation.startTime.lt(request.getEndTime()));
        BooleanExpression cond2 = reservation.endTime.gt(request.getStartTime()).and(reservation.endTime.loe(request.getEndTime()));
        BooleanExpression condition = cond1.or(cond2);

        return condition;
    }


}
