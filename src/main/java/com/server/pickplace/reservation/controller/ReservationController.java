package com.server.pickplace.reservation.controller;

import com.server.pickplace.common.dto.SingleResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.reservation.dto.*;
import com.server.pickplace.reservation.error.ReservationErrorResult;
import com.server.pickplace.reservation.error.ReservationException;
import com.server.pickplace.reservation.repository.ReservationRepository;
import com.server.pickplace.reservation.service.ReservationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;

import static java.util.Base64.getUrlDecoder;

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
    public ResponseEntity<SingleResponse<Map>> reservationPage(@RequestHeader("Authorization") String accessToken,
                                                               @PathVariable("roomId") Long roomId) {

        Map<String, Object> payloadMap = getPayloadMap(accessToken); // 일단 토큰이 존재하고, 유효하다고 가정
        String email = (String) payloadMap.get("email");

        Map<String, Object> reservationPageMapByEmailAndRoomId = reservationService.getReservationPageMapByEmailAndRoomId(email, roomId);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), reservationPageMapByEmailAndRoomId));

    }

    @ApiOperation(tags = "4. Reservation", value = "카드 결제 검증", notes = "신용/체크카드 결제에서, 올바른 카드번호와 CVC 인지 검증한다.")
    @PostMapping("/card/validation")
    public ResponseEntity<SingleResponse> cardPayValidation(@RequestHeader("Authorization") String accessToken,
                                                               @RequestBody @Validated CardValidRequest cardValidRequest) {

        Map<String, Object> payloadMap = getPayloadMap(accessToken); // 일단 토큰이 존재하고, 유효하다고 가정
        String email = (String) payloadMap.get("email");

        String cardNum = cardValidRequest.getCardNum();
        validateCardByCardNum(cardNum);

        CardInfoResponse cardInfoResponse = reservationService.getCardInfoDto(email, cardNum);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), cardInfoResponse));
    }


    @ApiOperation(tags = "4. Reservation", value = "카드 결제 및 예약", notes = "신용/체크카드 결제와 실제 예약이 이루어진다.")
    @PostMapping("/card")
    public ResponseEntity cardPay(@RequestHeader("Authorization") String accessToken,
                                          @RequestBody @Validated CardPayRequest cardPayRequest) {

        Map<String, Object> payloadMap = getPayloadMap(accessToken); // 일단 토큰이 존재하고, 유효하다고 가정
        String email = (String) payloadMap.get("email");

        validateCardByCardNum(cardPayRequest.getCardNum());

        // 결제 + 예약( 단일 트랜잭션 )
        reservationService.payByCardAndReservation(email, cardPayRequest);

        return ResponseEntity.ok(null);

    }

    @ApiOperation(tags = "4. Reservation", value = "은행 별 가상계좌 받아오기", notes = "은행 별 가상계좌번호를 반환한다.")
    @PostMapping("/account/number")
    public ResponseEntity<SingleResponse> accountReturn(@RequestHeader("Authorization") String accessToken,
                                  @RequestBody BankRequest bankRequest) {

        String bankName = bankRequest.getBankName();
        String bankNum = getBankNumByBankName(bankName);

        BankResponse bankResponse = BankResponse.builder().bankName(bankName).bankNum(bankNum).build();

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), bankResponse));

    }

    @ApiOperation(tags = "4. Reservation", value = "계좌이체 및 예약", notes = "가상계좌를 통한 예약과 실제 예약이 이루어진다.")
    @PostMapping("/account")
    public ResponseEntity accountPay(@RequestHeader("Authorization") String accessToken,
                                                     @RequestBody @Validated AccountPayRequest accountPayRequest) {

        Map<String, Object> payloadMap = getPayloadMap(accessToken); // 일단 토큰이 존재하고, 유효하다고 가정
        String email = (String) payloadMap.get("email");

        String bankNumByBankName = getBankNumByBankName(accountPayRequest.getBankName());
        if (!bankNumByBankName.equals(accountPayRequest.getBankNum())) {
            throw new ReservationException(ReservationErrorResult.NO_MATCH_BANK_ACCOUNT);
        }

        reservationService.payByAccountAndReservation(email, accountPayRequest);

        return ResponseEntity.ok(null);

    }









    private String getBankNumByBankName(String bankName) {
        String bankNum;
        if (bankName.equals("국민은행")) {
            bankNum = "1020315-12108542";
        } else if (bankName.equals("하나은행")) {
            bankNum = "2020315-12108542";
        } else if (bankName.equals("신한은행")) {
            bankNum = "3020315-12108542";
        } else if (bankName.equals("우리은행")) {
            bankNum = "4020315-12108542";
        } else {
            throw new ReservationException(ReservationErrorResult.NO_EXIST_BANK);
        }

        return bankNum;
    }


    private int calculateVerificationCode(String cardNumber) {
        int sum = 0;
        boolean multiplyByTwo = false;

        for (int i = cardNumber.length() - 2; i >= 0; i--) {
            int digit = cardNumber.charAt(i) - '0';
            if (multiplyByTwo) {
                digit *= 2;
                if (digit > 9) {
                    digit = digit % 10 + digit / 10;
                }
            }
            sum += digit;
            multiplyByTwo = !multiplyByTwo;
        }

        int verificationCode = 10 - (sum % 10);
        return verificationCode % 10;
    }

    private void validateCardByCardNum(String cardNum) {
        int code = calculateVerificationCode(cardNum);
        int lastDigit = Integer.parseInt(cardNum.substring(cardNum.length() - 1));

        if (code != lastDigit) {
            throw new ReservationException(ReservationErrorResult.WRONG_CARD_NUMBER);
        }
    }


    private Map<String, Object> getPayloadMap(String accessToken) {

        String payloadJWT = accessToken.split("\\.")[1];
        Base64.Decoder decoder = getUrlDecoder();

        String payload = new String(decoder.decode(payloadJWT));
        JsonParser jsonParser = new BasicJsonParser();
        Map<String, Object> jsonArray = jsonParser.parseMap(payload);
        return jsonArray;
    }


}
