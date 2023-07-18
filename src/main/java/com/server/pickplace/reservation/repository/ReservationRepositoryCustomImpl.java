package com.server.pickplace.reservation.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.entity.QMember;
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

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.server.pickplace.member.entity.QMember.member;
import static com.server.pickplace.place.entity.QPlace.*;
import static com.server.pickplace.place.entity.QRoom.*;
import static com.server.pickplace.place.entity.QUnit.unit;
import static com.server.pickplace.reservation.entity.QReservation.reservation;

@RequiredArgsConstructor
@Slf4j
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

        // 1. 비어있는 호실 체크 ( 비어있는 호실이 없다면, 빈 호실 없음 반환 )

        Optional<Tuple> optionalTuple = Optional.ofNullable(
                        queryFactory
                        .select(room, unit)
                        .from(unit)
                        .join(unit.room, room)
                        .where(
                                room.id.eq(payRequest.getRoomId()),

                                dateCheckByRequest(payRequest).not().or(timeCheckByRequest(payRequest).not())
                        )
                        .orderBy(unit.id.asc())
                        .fetchOne()
                    );

        Tuple tuple = optionalTuple.orElseThrow(() -> new ReservationException(ReservationErrorResult.NO_EMPTY_ROOM));
        Room room = tuple.get(0, Room.class);
        Unit unit = tuple.get(1, Unit.class);
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
