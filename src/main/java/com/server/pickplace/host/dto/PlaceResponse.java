package com.server.pickplace.host.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceResponse {

    private String address;

    private String number;

    private String name;    // -> 까지는 보여지는 정보

    private Long id;


}
