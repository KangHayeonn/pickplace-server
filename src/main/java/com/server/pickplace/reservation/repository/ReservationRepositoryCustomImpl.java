package com.server.pickplace.reservation.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.pickplace.reservation.dto.MemberInfoResponse;
import com.server.pickplace.reservation.dto.PayInfoResponse;
import com.server.pickplace.reservation.dto.PlaceInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.server.pickplace.member.entity.QMember.member;
import static com.server.pickplace.place.entity.QPlace.*;
import static com.server.pickplace.place.entity.QRoom.*;

@RequiredArgsConstructor
@Slf4j
public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

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


}
