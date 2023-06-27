package com.server.pickplace.host.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomResponse {

    private String name;

    private Integer price;

}
