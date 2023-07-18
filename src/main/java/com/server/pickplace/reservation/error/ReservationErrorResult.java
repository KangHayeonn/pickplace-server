package com.server.pickplace.reservation.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorResult {

    WRONG_CARD_NUMBER(HttpStatus.BAD_REQUEST, "Invalid card number."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "Wrong password."),
    NO_EMPTY_ROOM(HttpStatus.BAD_REQUEST, "No empty room"),
    NO_EXIST_BANK(HttpStatus.BAD_REQUEST, "Not exist bank"),
    NO_MATCH_BANK_ACCOUNT(HttpStatus.BAD_REQUEST, "Does not match between bank and account."),
    QR_CODE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurs in making QR code."),
    NON_EXIST_QR_PAYMENT_CODE(HttpStatus.BAD_REQUEST, "Not exist QR payment code."),
    QR_AUTH_NOT_COMPLETE(HttpStatus.BAD_REQUEST, "QR authentication is not completed.");



    private final HttpStatus httpStatus;
    private final String message;

}
