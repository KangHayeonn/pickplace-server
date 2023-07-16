package com.server.pickplace.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import java.util.Map;

@Getter
@Builder
public class PlaceResponse {

    @JsonProperty("placeId")
    private Long id;

    @JsonProperty("placeAddress")
    private Map<String, Object> address;

    @JsonProperty("placeName")
    private String name;

    @JsonProperty("placeRating")
    private Float rating;

    @JsonProperty("placeReviewCnt")
    private Integer reviewCount;


}
