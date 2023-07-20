package com.server.pickplace.host.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HostErrorResult {

    NOT_EXIST_PLACE(HttpStatus.BAD_REQUEST, "Place ID that does not exist."),
    NOT_EXIST_RESERVATION(HttpStatus.BAD_REQUEST, "RESERVATION ID that does not exist."),
    NO_PERMISSION(HttpStatus.BAD_REQUEST, "권한 없음");

    private final HttpStatus httpStatus;
    private final String message;

}
