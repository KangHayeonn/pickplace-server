package com.server.pickplace.search.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SearchErrorResult {

    NOT_EXIST_PLACE(HttpStatus.BAD_REQUEST, "Place ID that does not exist.");

    private final HttpStatus httpStatus;
    private final String message;

}


