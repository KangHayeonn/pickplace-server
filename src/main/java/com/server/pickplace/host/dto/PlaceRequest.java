package com.server.pickplace.host.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceRequest {

    private String placeName;

    private String placePhone;

    private String placeAddress;

}
