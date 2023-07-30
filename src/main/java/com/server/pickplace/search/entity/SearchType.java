package com.server.pickplace.search.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SearchType {

    추천순("추천 순"),

    낮은가격순("낮은 가격순"),

    높은가격순("높은 가격순");

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
