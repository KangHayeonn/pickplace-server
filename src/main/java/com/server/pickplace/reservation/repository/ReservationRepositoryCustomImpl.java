package com.server.pickplace.reservation.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.entity.QMember;
import com.server.pickplace.place.entity.*;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.server.pickplace.member.entity.QMember.member;
import static com.server.pickplace.place.entity.QCategoryPlace.*;
import static com.server.pickplace.place.entity.QPlace.*;
import static com.server.pickplace.place.entity.QRoom.*;
import static com.server.pickplace.place.entity.QUnit.unit;

@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public Map<String, Object> getReservationPageMapByEmailAndRoomId(Long id, Long roomId) {

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
                .where(member.id.eq(id))
                .fetchOne();

        reservationPageMap.put("place", placeInfoResponse);
        reservationPageMap.put("payment", payInfoResponse);
        reservationPageMap.put("member", memberInfoResponse);

        return reservationPageMap;
    }

    @Override
    public void makeReservation(Long id, PayRequest payRequest) {

        // 0. 요청 검증
        validTimeCondition(payRequest);

        // 1. 모든 호실ID 리스트
        List<Long> allUnitIdList = queryFactory
                .select(unit.id)
                .from(room)
                .join(room.units, unit).on(room.id.eq(payRequest.getRoomId()))
                .fetch();

        // 2. 시간조건 걸리는 unit 리스트
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String stringStartDateTime = payRequest.getCheckInTime().format(formatter);
        String stringEndDateTime = payRequest.getCheckOutTime().format(formatter);
        String sql = String.format("""
                    select distinct unit_id
                    from
                    		(
                            select unit_tb.unit_id, CONCAT(start_date, ' ', start_time) AS start_datetime, CONCAT(end_date, ' ', end_time) AS end_datetime
                    		from room_tb
                    		join unit_tb on (room_tb.room_id = %d) and (room_tb.room_id = unit_tb.room_id)
                    		join reservation_tb on unit_tb.unit_id = reservation_tb.unit_id
                            ) sub_query
                    
                    where '%s' > start_datetime and '%s' < end_datetime
                """, payRequest.getRoomId(), stringEndDateTime, stringStartDateTime);

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
        Member member = getMember(id);

        Member member1 = getMemberByRoom(room);
        if (member == member1) {
            throw new ReservationException(ReservationErrorResult.WRONG_CUSTOMER);
        }


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
    public String saveQRPaymentInformation(Long id, Integer roomPrice) {

        String uuid = UUID.randomUUID().toString();

        QRPaymentInfomation qrPaymentInfomation = QRPaymentInfomation.builder()
                .qrPaymentCode(uuid)
                .memberId(id)
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


    private Member getMemberByRoom(Room room) {
        return queryFactory.select(QMember.member)
                .from(QRoom.room)
                .join(QRoom.room.place, place)
                .join(place.member, QMember.member)
                .where(QRoom.room.eq(room))
                .fetchOne();
    }


    private Member getMember(Long id) {
        return queryFactory
                .select(member)
                .from(member)
                .where(member.id.eq(id))
                .fetchOne();
    }


    private CategoryStatus getCategoryStatusByPayRequest(PayRequest payRequest) {
        Category category = queryFactory
                .select(QCategory.category)
                .from(place)
                .join(place.rooms, room).on(room.id.eq(payRequest.getRoomId()))
                .join(place.categories, categoryPlace)
                .join(categoryPlace.category, QCategory.category)
                .fetchOne();
        CategoryStatus status = category.getStatus();

        return status;
    }


    private void validTimeCondition(PayRequest payRequest) {

        // 1. 플레이스의 카테고리
        // 2. 카테고리가 날짜만 받는 형태라면, 1. 날짜가 이후일 것 2. 시간이 15시 ~ 10시일 것
        // 3. 카테고리가 시간만 받는 형태라면(당일), 1. 날짜가 똑같음  2. 시작 시간이 15시거나 이후 3. 끝 시간이 23시거나 이전 4. 1시간 단위


        List<CategoryStatus> categoryStatusList = new ArrayList<>(Arrays.asList(CategoryStatus.Hotel, CategoryStatus.Pension, CategoryStatus.GuestHouse));

        CategoryStatus categoryStatus = getCategoryStatusByPayRequest(payRequest);

        if (categoryStatusList.contains(categoryStatus)) {

            datePlaceConditionCheck(payRequest);

        } else {

            timePlaceConditionCheck(payRequest);

        }
    }


    private void datePlaceConditionCheck(PayRequest payRequest) {
        if (payRequest.getStartDate().isEqual(payRequest.getEndDate()) || payRequest.getStartDate().isAfter(payRequest.getEndDate())) {
            throw new ReservationException(ReservationErrorResult.WRONG_DATE_CONDITION);
        } else if (!payRequest.getStartTime().equals(LocalTime.of(15, 00)) || !payRequest.getEndTime().equals(LocalTime.of(10, 00))) {
            throw new ReservationException(ReservationErrorResult.WRONG_TIME_CONDITION);
        }
    }

    // 3. 카테고리가 시간만 받는 형태라면(당일), 1. 날짜가 똑같음  2. 시작 시간이 15시거나 이후 3. 끝 시간이 23시거나 이전 4. 시작 시간이 끝 시간 전 5. 1시간 단위

    private void timePlaceConditionCheck(PayRequest payRequest) {

        //
        if (!payRequest.getStartDate().equals(payRequest.getEndDate())) {
            throw new ReservationException(ReservationErrorResult.WRONG_DATE_CONDITION);
        } else if (payRequest.getStartTime().isBefore(LocalTime.of(15, 00))) {
                throw new ReservationException(ReservationErrorResult.WRONG_TIME_CONDITION);
        } else if (payRequest.getEndTime().isAfter(LocalTime.of(23, 00))) {
            throw new ReservationException(ReservationErrorResult.WRONG_TIME_CONDITION);
        } else if (!payRequest.getEndTime().isAfter(payRequest.getStartTime())) {
            throw new ReservationException(ReservationErrorResult.WRONG_TIME_CONDITION);
        } else if (payRequest.getStartTime().getMinute() != 0 || payRequest.getEndTime().getMinute() != 0) {
            throw new ReservationException(ReservationErrorResult.WRONG_TIME_CONDITION);
        }


        }
    }

