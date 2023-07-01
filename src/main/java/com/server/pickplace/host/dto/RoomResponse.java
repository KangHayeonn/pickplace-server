package com.server.pickplace.host.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomResponse {

    @JsonProperty("roomName")
    private String name;

    @JsonProperty("roomPrice")
    private Integer price;

}
