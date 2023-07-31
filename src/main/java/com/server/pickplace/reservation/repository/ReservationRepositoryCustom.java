package com.server.pickplace.reservation.repository;


import com.server.pickplace.reservation.dto.PayRequest;
import com.server.pickplace.reservation.entity.QRPaymentInfomation;
import com.server.pickplace.reservation.entity.QRStatus;

import java.util.Map;

public interface ReservationRepositoryCustom {

    Map<String, Object> getReservationPageMapByEmailAndRoomId(Long id, Long roomId);

    void makeReservation(Long id, PayRequest cardPayRequest);

    String saveQRPaymentInformation(Long id, Integer roomPrice);

    void changeQREntityStatus(QRPaymentInfomation qrPaymentInfomation, QRStatus status);

    void roomIdCheck(Long roomId);
}
