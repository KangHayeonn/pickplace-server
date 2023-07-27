package com.server.pickplace.host.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HostErrorResult {

    NOT_EXIST_PLACE(HttpStatus.BAD_REQUEST, "Place ID that does not exist."),
    NOT_EXIST_RESERVATION(HttpStatus.BAD_REQUEST, "RESERVATION ID that does not exist."),
    NOT_EXIST_ROOM(HttpStatus.BAD_REQUEST, "ROOM ID is not exist"),
    CANT_CHANGE_ROOM_AMOUNT(HttpStatus.BAD_REQUEST, "호실 갯수를 변경할 수 없습니다. 호실 별 예약 목록을 확인해주세요."),
    NO_PERMISSION(HttpStatus.BAD_REQUEST, "권한 없음");

    private final HttpStatus httpStatus;
    private final String message;

}
