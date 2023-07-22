package com.server.pickplace.search.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SearchErrorResult {

    NOT_EXIST_PLACE(HttpStatus.BAD_REQUEST, "Place ID that does not exist."),
    DATE_TIME_NULL_CHECK(HttpStatus.BAD_REQUEST, "올바르지 못한 날짜 혹은 시간 형식");


    private final HttpStatus httpStatus;
    private final String message;

}


