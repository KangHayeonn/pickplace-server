package com.server.pickplace.reservation.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorResult {

    WRONG_CARD_NUMBER(HttpStatus.BAD_REQUEST, "유효하지 않은 카드 번호입니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),
    NO_EMPTY_ROOM(HttpStatus.BAD_REQUEST, "빈 방이 없습니다."),
    NO_EXIST_BANK(HttpStatus.BAD_REQUEST, "존재하지 않는 은행입니다."),
    NO_MATCH_BANK_ACCOUNT(HttpStatus.BAD_REQUEST, "일치하지 않는 은행과 계좌번호입니다."),
    QR_CODE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "QR코드 생성중 오류가 발생했습니다."),
    NON_EXIST_QR_PAYMENT_CODE(HttpStatus.BAD_REQUEST, "QR 결제 코드가 존재하지 않습니다."),
    NO_EXIST_ROOM_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 방 번호입니다."),
    WRONG_DATE_CONDITION(HttpStatus.BAD_REQUEST, "날짜 조건을 확인해주세요."),
    WRONG_TIME_CONDITION(HttpStatus.BAD_REQUEST, "시간 조건을 확인해주세요"),
    QR_AUTH_NOT_COMPLETE(HttpStatus.BAD_REQUEST, "QR 인증이 완료되지 않았습니다.");


    private final HttpStatus httpStatus;
    private final String message;

}
