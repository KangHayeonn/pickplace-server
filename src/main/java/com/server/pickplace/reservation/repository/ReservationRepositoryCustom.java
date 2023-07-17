package com.server.pickplace.reservation.repository;


import com.server.pickplace.reservation.dto.PayRequest;

import java.util.Map;

public interface ReservationRepositoryCustom {

    Map<String, Object> getReservationPageMapByEmailAndRoomId(String email, Long roomId);

    void makeReservation(String email, PayRequest cardPayRequest);
}
