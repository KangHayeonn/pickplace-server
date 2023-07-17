package com.server.pickplace.reservation.repository;


import java.util.Map;

public interface ReservationRepositoryCustom {

    Map<String, Object> getReservationPageMapByEmailAndRoomId(String email, Long roomId);

}
