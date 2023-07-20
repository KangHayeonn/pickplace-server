package com.server.pickplace.place.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.server.pickplace.reservation.entity.ReservationStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CategoryStatus {

    Hotel("호텔/리조트"),

    Pension("펜션"),

    GuestHouse("게스트하우스"),

    StudyRoom("스터디룸"),

    PartyRoom("파티룸");

    private final String value;

    @JsonCreator
    public static CategoryStatus from(String value) {
        for (CategoryStatus status : CategoryStatus.values()) {
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
