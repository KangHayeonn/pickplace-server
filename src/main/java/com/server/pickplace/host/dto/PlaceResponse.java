package com.server.pickplace.host.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceResponse {

    @JsonProperty("placeAddress")
    private String address;

    @JsonProperty("placePhone")
    private String number;

    @JsonProperty("placeName")
    private String name;

    @JsonProperty("placeId")
    private Long id;


}
