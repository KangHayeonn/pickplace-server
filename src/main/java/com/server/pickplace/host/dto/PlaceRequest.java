package com.server.pickplace.host.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRequest {

    @NotBlank(message = "{place.name.NotBlank}")
    @JsonProperty("placeName")
    private String name;

    @Pattern(message = "형식에 맞는 전화번호를 입력해주세요.", regexp = "^\\d{9,11}$")
    @JsonProperty("placePhone")
    private String number;

    @NotBlank(message = "{address.NotBlank}")
    @JsonProperty("placeAddress")
    private String address;

    @Positive
    @NotNull(message = "{x.NotNull}")
    @JsonProperty("placeXaxis")
    private Double x;

    @Positive
    @NotNull(message = "{y.NotNull}")
    @JsonProperty("placeYaxis")
    private Double y;




}
