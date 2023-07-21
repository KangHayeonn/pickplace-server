package com.server.pickplace.reservation.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.server.pickplace.reservation.dto.*;
import com.server.pickplace.reservation.entity.BankStatus;
import com.server.pickplace.reservation.entity.QRPaymentInfomation;
import com.server.pickplace.reservation.entity.QRStatus;
import com.server.pickplace.reservation.error.ReservationErrorResult;
import com.server.pickplace.reservation.error.ReservationException;
import com.server.pickplace.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import static java.util.Base64.getUrlDecoder;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;

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

        String memberPassword = reservationRepository.findMemberPasswordByEmail(email);

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


    public String getBankNumByBankName(String bankName) {

        String bankNum;

        for (BankStatus bankStatus : BankStatus.values()) {
            if (bankStatus.getBankName().equals(bankName)) {
                bankNum = bankStatus.getBankNum();
                return bankNum;
            }
        }

        throw new ReservationException(ReservationErrorResult.NO_EXIST_BANK);
    }


    public QRPaymentInfomation getQREntityByDto(String qrPaymentCode, QRPasswordRequest qrPasswordRequest) {

        Optional<QRPaymentInfomation> optionalQREntity = reservationRepository.findQREntityByQRPaymentCode(qrPaymentCode);
        QRPaymentInfomation qrPaymentInfomation = optionalQREntity.orElseThrow(() -> new ReservationException(ReservationErrorResult.NON_EXIST_QR_PAYMENT_CODE));

        String email = qrPaymentInfomation.getEmail();

        String memberPassword = reservationRepository.findMemberPasswordByEmail(email);

        if (!memberPassword.equals(qrPasswordRequest.getQrPassword())) {
            throw new ReservationException(ReservationErrorResult.WRONG_PASSWORD);
        }

        return qrPaymentInfomation;
    }

    public QRPaymentInfomation qrCheckByDto(QRPayRequest qrPayRequest) {

        Optional<QRPaymentInfomation> optionalQREntity = reservationRepository.findQREntityByQRPaymentCode(qrPayRequest.getQrPaymentCode());
        QRPaymentInfomation qrPaymentInfomation = optionalQREntity.orElseThrow(() -> new ReservationException(ReservationErrorResult.NON_EXIST_QR_PAYMENT_CODE));
        if (!qrPaymentInfomation.getStatus().equals(QRStatus.APPROVAL)) {
            throw new ReservationException(ReservationErrorResult.QR_AUTH_NOT_COMPLETE);
        }

        return qrPaymentInfomation;
    }


    public String getPayloadMapAndGetEmail(String accessToken) {

        String payloadJWT = accessToken.split("\\.")[1];
        Base64.Decoder decoder = getUrlDecoder();

        String payload = new String(decoder.decode(payloadJWT));
        JsonParser jsonParser = new BasicJsonParser();
        Map<String, Object> jsonArray = jsonParser.parseMap(payload);

        String email = (String) jsonArray.get("sub");

        return email;
    }

}
