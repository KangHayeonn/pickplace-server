package com.server.pickplace.host.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.pickplace.place.entity.CategoryStatus;
import lombok.*;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class PlaceResponse {

    @JsonProperty("placeAddress")
    private String address;

    @JsonProperty("placePhone")
    private String number;

    @JsonProperty("placeName")
    private String name;

    @JsonProperty("placeId")
    private Long id;

    @JsonProperty("placeCategory")
    private CategoryStatus categoryStatus;

}
