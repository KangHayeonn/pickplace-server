package com.server.pickplace.reservation.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.server.pickplace.member.entity.MemberRole;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReservationStatus {

    PAYMENT("Payment"), // 결제 완료

    APPROVAL("Approval"), // 사장님 승인 완료 -> 필요할지는 의문

    CANCEL("Cancel"); // 예약 취소

    // 추후 추가 필요

    private final String value;

    @JsonCreator
    public static ReservationStatus from(String value) {
        for (ReservationStatus status : ReservationStatus.values()) {
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
