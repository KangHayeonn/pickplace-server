package com.server.pickplace.search.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SearchErrorResult {

    NOT_EXIST_PLACE(HttpStatus.BAD_REQUEST, "존재하지 않는 플레이스 ID입니다."),
    DATE_TIME_NULL_CHECK(HttpStatus.BAD_REQUEST, "올바르지 못한 날짜 혹은 시간 형식입니다"),
    TIME_CONDITION_CHECK(HttpStatus.BAD_REQUEST, "플레이스 종류와 시간 조건을 확인해주세요.");

    private final HttpStatus httpStatus;
    private final String message;

}


