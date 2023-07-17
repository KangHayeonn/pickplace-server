package com.server.pickplace.reservation.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorResult {

    WRONG_CARD_NUMBER(HttpStatus.BAD_REQUEST, "Invalid card number."),
    WRONG_CARD_PASSWORD(HttpStatus.BAD_REQUEST, "Wrong card password."),
    NO_EMPTY_ROOM(HttpStatus.BAD_REQUEST, "No empty room");


    private final HttpStatus httpStatus;
    private final String message;

}
