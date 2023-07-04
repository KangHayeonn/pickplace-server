package com.server.pickplace.common.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorResult {

    UNKNOWN_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Exception"),
    VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST, "Validation Exception"),
    MISSING_REQUEST_HEADER(HttpStatus.BAD_REQUEST, "Request header does not exist");

    private final boolean success = false;
    private final HttpStatus httpStatus;
    private final String message;

}
