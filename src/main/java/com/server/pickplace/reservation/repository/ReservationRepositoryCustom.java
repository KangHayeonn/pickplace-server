package com.server.pickplace.reservation.repository;


import com.server.pickplace.reservation.dto.PayRequest;
import com.server.pickplace.reservation.entity.QRPaymentInfomation;
import com.server.pickplace.reservation.entity.QRStatus;

import java.util.Map;

public interface ReservationRepositoryCustom {

    Map<String, Object> getReservationPageMapByEmailAndRoomId(String email, Long roomId);

    void makeReservation(String email, PayRequest cardPayRequest);

    String saveQRPaymentInformation(String email, Integer roomPrice);

    void changeQREntityStatus(QRPaymentInfomation qrPaymentInfomation, QRStatus status);
}
