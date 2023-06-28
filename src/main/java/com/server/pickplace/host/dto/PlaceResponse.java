package com.server.pickplace.host.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceResponse {

    private String address;

    private String number;

    private String name;

    private Long id;


}
