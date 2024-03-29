package com.server.pickplace.reservation.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.server.pickplace.common.service.CommonService;
import com.server.pickplace.reservation.dto.*;
import com.server.pickplace.reservation.entity.BankStatus;
import com.server.pickplace.reservation.entity.QRPaymentInfomation;
import com.server.pickplace.reservation.entity.QRStatus;
import com.server.pickplace.reservation.error.ReservationErrorResult;
import com.server.pickplace.reservation.error.ReservationException;
import com.server.pickplace.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService extends CommonService {

    private final ReservationRepository reservationRepository;
    private final PasswordEncoder pwEncoder;

    public Map<String, Object> getReservationPageMapByEmailAndRoomId(Long id, Long roomId) {

        Map<String, Object> reservationPageMapByEmailAndRoomId = reservationRepository.getReservationPageMapByEmailAndRoomId(id, roomId);// 그냥 위임만

        return reservationPageMapByEmailAndRoomId;
    }

    public CardInfoResponse getCardInfoDto(Long id, String cardNum) {

        String memberName = reservationRepository.findMemberNameById(id);

        CardInfoResponse cardInfoResponse = CardInfoResponse.builder().name(memberName).number(cardNum).build();

        return cardInfoResponse;
    }

    public void payByCardAndReservation(Long id, CardPayRequest cardPayRequest) {

        // 결제
        String inputCardPassword = cardPayRequest.getCardPassword();

        String memberPassword = reservationRepository.findMemberPasswordById(id);

        if (!pwEncoder.matches(inputCardPassword, memberPassword)) {
            throw new ReservationException(ReservationErrorResult.WRONG_PASSWORD);
        }


        // 예약
        reservationRepository.makeReservation(id, cardPayRequest);

    }


    public void payByAccountAndReservation(Long id, AccountPayRequest accountPayRequest) {

        // 결제
        String inputAccountPassword = accountPayRequest.getAccountPassword();

        String memberPassword = reservationRepository.findMemberPasswordById(id);

        if (!pwEncoder.matches(inputAccountPassword, memberPassword)) {
            throw new ReservationException(ReservationErrorResult.WRONG_PASSWORD);
        }

        // 예약
        reservationRepository.makeReservation(id, accountPayRequest);

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

        Long id = qrPaymentInfomation.getMemberId();

        String memberPassword = reservationRepository.findMemberPasswordById(id);
        String inputQrPassword = qrPasswordRequest.getQrPassword();


        if (!pwEncoder.matches(inputQrPassword, memberPassword)) {
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

}
