package com.server.pickplace.review.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorResult {

    WRONG_RESERVATION_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 예약ID"),
    NO_PERMISSION(HttpStatus.BAD_REQUEST, "권한 없음"),
    REVIEW_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "리뷰 이미 존재함"),
    NO_EXIST_PLACE_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 플레이스 ID"),
    NO_EXIST_REVIEW_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 리뷰 ID");

    private final HttpStatus httpStatus;
    private final String message;


}
