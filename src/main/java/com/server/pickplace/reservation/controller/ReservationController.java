package com.server.pickplace.reservation.controller;

import com.server.pickplace.common.dto.SingleResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.reservation.dto.*;
import com.server.pickplace.reservation.entity.QRPaymentInfomation;
import com.server.pickplace.reservation.entity.QRStatus;
import com.server.pickplace.reservation.error.ReservationErrorResult;
import com.server.pickplace.reservation.error.ReservationException;
import com.server.pickplace.reservation.repository.ReservationRepository;
import com.server.pickplace.reservation.service.ReservationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/reservation")
@Slf4j
public class ReservationController {

    private final ResponseService responseService;
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;

    @ApiOperation(tags = "4. Reservation", value = "예약페이지 접근", notes = "상세페이지 공간안내/예약에서 예약 버튼을 눌렀을때 이동하는 페이지")
    @GetMapping("/{roomId}")
    public ResponseEntity<SingleResponse<Map>> reservationPage(@RequestHeader("accessToken") String accessToken,
                                                               @PathVariable("roomId") Long roomId) {

        String email = reservationService.getPayloadMapAndGetEmail(accessToken);

        reservationRepository.roomIdCheck(roomId);
        
        Map<String, Object> reservationPageMapByEmailAndRoomId = reservationService.getReservationPageMapByEmailAndRoomId(email, roomId);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), reservationPageMapByEmailAndRoomId));

    }

    @ApiOperation(tags = "4. Reservation", value = "카드 결제 검증", notes = "신용/체크카드 결제에서, 올바른 카드번호와 CVC 인지 검증한다.")
    @PostMapping("/card/validation")
    public ResponseEntity<SingleResponse> cardPayValidation(@RequestHeader("accessToken") String accessToken,
                                                            @RequestBody @Validated CardValidRequest cardValidRequest) {

        String email = reservationService.getPayloadMapAndGetEmail(accessToken);

        String cardNum = cardValidRequest.getCardNum();

        CardInfoResponse cardInfoResponse = reservationService.getCardInfoDto(email, cardNum);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), cardInfoResponse));
    }


    @ApiOperation(tags = "4. Reservation", value = "카드 결제 및 예약", notes = "신용/체크카드 결제와 실제 예약이 이루어진다.")
    @PostMapping("/card")
    public ResponseEntity cardPay(@RequestHeader("accessToken") String accessToken,
                                  @RequestBody CardPayRequest cardPayRequest) {

        String email = reservationService.getPayloadMapAndGetEmail(accessToken);

        // 결제 + 예약( 단일 트랜잭션 )
        reservationService.payByCardAndReservation(email, cardPayRequest);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), new Object()));

    }

    @ApiOperation(tags = "4. Reservation", value = "은행 별 가상계좌 받아오기", notes = "은행 별 가상계좌번호를 반환한다.")
    @PostMapping("/account/number")
    public ResponseEntity<SingleResponse> accountReturn(@RequestBody BankRequest bankRequest) {

        String bankName = bankRequest.getBankName();
        String bankNum = reservationService.getBankNumByBankName(bankName);

        BankResponse bankResponse = BankResponse.builder().bankName(bankName).bankNum(bankNum).build();

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), bankResponse));

    }

    @ApiOperation(tags = "4. Reservation", value = "계좌이체 및 예약", notes = "가상계좌를 통한 예약과 실제 예약이 이루어진다.")
    @PostMapping("/account")
    public ResponseEntity accountPay(@RequestHeader("accessToken") String accessToken,
                                     @RequestBody @Validated AccountPayRequest accountPayRequest) {

        String email = reservationService.getPayloadMapAndGetEmail(accessToken);


        String bankNumByBankName = reservationService.getBankNumByBankName(accountPayRequest.getBankName());
        if (!bankNumByBankName.equals(accountPayRequest.getBankNum())) {
            throw new ReservationException(ReservationErrorResult.NO_MATCH_BANK_ACCOUNT);
        }

        reservationService.payByAccountAndReservation(email, accountPayRequest);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), new Object()));

    }

    @ApiOperation(tags = "4. Reservation", value = "QR 코드 응답", notes = "페이지에 보여지는 QR코드를 리턴한다.")
    @PostMapping(value = "/qrcode/image")
    public ResponseEntity<SingleResponse> qrCodeImage(@RequestHeader("accessToken") String accessToken,
                                                      @Validated @RequestBody QRImageReqeust qrImageReqeust) {

        String email = reservationService.getPayloadMapAndGetEmail(accessToken);

        String uuid = reservationRepository.saveQRPaymentInformation(email, qrImageReqeust.getRoomPrice());

        String url = "https://pickplace.kr/payment?code=" + uuid;  // 결제 비밀번호 입력하는 링크로...

        QRImageResponse qrImageResponse = reservationService.getQRImageResponse(qrImageReqeust, uuid, url);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), qrImageResponse));


    }


    @ApiOperation(tags = "4. Reservation", value = "QR 코드 결제 페이지 가격 정보", notes = "모바일 환경에서 보여지는 비밀번호 입력 페이지에, 결제 가격을 보여준다.")
    @GetMapping(value = "/qrcode/{qrPaymentCode}")
    public ResponseEntity<SingleResponse<QRPriceResponse>> qrCodePrice(@PathVariable("qrPaymentCode") String qrPaymentCode) {

        Optional<Integer> optionalQRPrice = reservationRepository.findQRPriceByQRPaymentCode(qrPaymentCode);
        Integer price = optionalQRPrice.orElseThrow(() -> new ReservationException(ReservationErrorResult.NON_EXIST_QR_PAYMENT_CODE));

        QRPriceResponse qrPriceResponse = QRPriceResponse.builder().roomPrice(price).build();

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), qrPriceResponse));

    }

    @ApiOperation(tags = "4. Reservation", value = "QR 코드 비밀번호 인증", notes = "결제 비밀번호를 받아, 일치한다면 결제/예약을 진행할 수 있도록 한다.")
    @PostMapping(value = "/qrcode/{qrPaymentCode}")
    public ResponseEntity qrPassword(@PathVariable("qrPaymentCode") String qrPaymentCode,
                                                                      @Validated @RequestBody QRPasswordRequest qrPasswordRequest) {

        // qr 엔티티 반환 및 예외 처리

        QRPaymentInfomation qrPaymentInfomation = reservationService.getQREntityByDto(qrPaymentCode, qrPasswordRequest);


        // 비밀번호 일치 -> 결제 상태를 '인증됨'으로

        reservationRepository.changeQREntityStatus(qrPaymentInfomation, QRStatus.APPROVAL);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), new Object()));

    }

    @ApiOperation(tags = "4. Reservation", value = "QR 결제", notes = "모바일로 QR 비밀번호 인증을 한 후, PC에서 다음 버튼을 눌러 실 결제를 진행한다.")
    @PostMapping(value = "/qrcode")
    public ResponseEntity qrPassword(@RequestHeader("accessToken") String accessToken,
                                     @Validated @RequestBody QRPayRequest qrPayRequest) {

        String email = reservationService.getPayloadMapAndGetEmail(accessToken);

        // 1. QR을 통해 인증 했는지 확인
        QRPaymentInfomation qrPaymentInfomation = reservationService.qrCheckByDto(qrPayRequest);


        // 2. 예약
        reservationRepository.makeReservation(email, qrPayRequest);
        reservationRepository.changeQREntityStatus(qrPaymentInfomation, QRStatus.PAYMENT);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), new Object()));
    }




}
