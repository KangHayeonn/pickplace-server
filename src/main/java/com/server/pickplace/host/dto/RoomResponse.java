package com.server.pickplace.host.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.pickplace.place.entity.Room;
import lombok.*;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class RoomResponse {

    @JsonProperty("roomName")
    private String name;

    @JsonProperty("roomPrice")
    private Integer price;

    @JsonProperty("roomAmount")
    private Integer amount;

    @JsonProperty("roomMaxNum")
    private Integer peopleNum;


}
