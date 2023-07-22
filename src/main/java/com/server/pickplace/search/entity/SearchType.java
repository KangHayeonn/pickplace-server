package com.server.pickplace.search.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SearchType {

    BASIC("추천 순"),

    LOW_PRICE("낮은 가격순"),

    HIGH_PRICE("높은 가격순");

    // 추후 추가 필요

    private final String value;

    @JsonCreator
    public static SearchType from(String value) {
        for (SearchType status : SearchType.values()) {
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
