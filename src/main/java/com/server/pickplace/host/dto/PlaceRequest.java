package com.server.pickplace.host.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRequest {

    @NotBlank  // Null, 빈 문자열, 스페이스만 있는 문자열 불가
    @JsonProperty("placeName")
    private String name;

    @Pattern(regexp = "^\\d{9,11}$")
    @JsonProperty("placePhone")
    private String number;

    @NotBlank
    @JsonProperty("placeAddress")
    private String address;

    @Positive
    @JsonProperty("placeXaxis")
    private Double x;

    @Positive
    @JsonProperty("placeYaxis")
    private Double y;




}
