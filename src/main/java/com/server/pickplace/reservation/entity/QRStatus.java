package com.server.pickplace.reservation.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum QRStatus {

    WAITING("Waiting"), // 대기중

    APPROVAL("Approval"), // 인증됨 : 결제 및 예약 대기

    PAYMENT("Payment"); // 결제 및 예약 완료

    // 추후 추가 필요

    private final String value;


    @JsonCreator
    public static QRStatus from(String value) {
        for (QRStatus status : QRStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }


}


