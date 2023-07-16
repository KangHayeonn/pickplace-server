package com.server.pickplace.host.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceRequest {

    @NotBlank  // Null, 빈 문자열, 스페이스만 있는 문자열 불가
    @Size(max = 20)
    private String placeName;

    @Pattern(regexp = "^\\d{9,11}$")
    private String placePhone;

    @NotBlank
    @Size(max = 255)
    private String placeAddress;

}
