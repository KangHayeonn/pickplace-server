package com.server.pickplace.host.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HostErrorResult {

    NOT_EXIST_PLACE(HttpStatus.BAD_REQUEST, "존재하지 않는 플레이스 ID입니다."),
    NOT_EXIST_RESERVATION(HttpStatus.BAD_REQUEST, "존재하지 않는 예약 ID입니다."),
    NOT_EXIST_ROOM(HttpStatus.BAD_REQUEST, "존재 하지 않는 방 ID입니다."),
    CANT_CHANGE_ROOM_AMOUNT(HttpStatus.BAD_REQUEST, "호실 갯수를 변경할 수 없습니다. 호실 별 예약 목록을 확인해주세요."),
    NO_PERMISSION(HttpStatus.BAD_REQUEST, "권한 없음");

    private final HttpStatus httpStatus;
    private final String message;

}
