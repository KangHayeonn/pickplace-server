package com.server.pickplace.place.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.server.pickplace.reservation.entity.ReservationStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TagStatus {

    PetFriendly("애견동반"),

    BabyWith("아이동반"),

    Lover("연인추천"),  // 연인 추천

    Family("가족추천"),

    Friend("친구추천"),

    Exciting("신나는"),

    Quiet("조용한"),

    Relax("편안한");


    private final String value;

    @JsonCreator
    public static TagStatus from(String value) {
        for (TagStatus status : TagStatus.values()) {
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
