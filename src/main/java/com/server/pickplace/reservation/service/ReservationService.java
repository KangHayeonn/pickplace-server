package com.server.pickplace.reservation.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.server.pickplace.reservation.dto.*;
import com.server.pickplace.reservation.error.ReservationErrorResult;
import com.server.pickplace.reservation.error.ReservationException;
import com.server.pickplace.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ModelMapper modelMapper;

    public Map<String, Object> getReservationPageMapByEmailAndRoomId(String email, Long roomId) {

        Map<String, Object> reservationPageMapByEmailAndRoomId = reservationRepository.getReservationPageMapByEmailAndRoomId(email, roomId);// 그냥 위임만

        return reservationPageMapByEmailAndRoomId;
    }

    public CardInfoResponse getCardInfoDto(String email, String cardNum) {

        String memberName = reservationRepository.findMemberNameByEmail(email);

        CardInfoResponse cardInfoResponse = CardInfoResponse.builder().name(memberName).number(cardNum).build();

        return cardInfoResponse;
    }

    public void payByCardAndReservation(String email, CardPayRequest cardPayRequest) {

        // 결제
        String inputCardPassword = cardPayRequest.getCardPassword();

        String memberPassword = reservationRepository.findMemberPasswordByEmail(email);

        if (!inputCardPassword.equals(memberPassword)) {
            throw new ReservationException(ReservationErrorResult.WRONG_PASSWORD);
        }

        // 예약
        reservationRepository.makeReservation(email, cardPayRequest);

    }


    public void payByAccountAndReservation(String email, AccountPayRequest accountPayRequest) {

        // 결제
        String inputAccountPassword = accountPayRequest.getAccountPassword();

        String memberPassword = reservationRepository.findMemberNameByEmail(email);

        if (!inputAccountPassword.equals(memberPassword)) {
            throw new ReservationException(ReservationErrorResult.WRONG_PASSWORD);
        }

        // 예약
        reservationRepository.makeReservation(email, accountPayRequest);

    }


    public QRImageResponse getQRImageResponse(QRImageReqeust qrImageReqeust, String uuid, String url) {
        int width = qrImageReqeust.getWidth();
        int height = qrImageReqeust.getHeight();

        try {
            BitMatrix encode = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(encode, "PNG", out);

            QRImageResponse qrImageResponse = QRImageResponse.builder().qrImage(out.toByteArray()).qrPaymentCode(uuid).build();

            return qrImageResponse;

        } catch (Exception e) {
            throw new ReservationException(ReservationErrorResult.QR_CODE_EXCEPTION);
        }
    }

}
