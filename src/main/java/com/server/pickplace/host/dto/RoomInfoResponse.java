package com.server.pickplace.host.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class RoomInfoResponse {

    @JsonProperty("roomId")
    private Long id;

    @JsonProperty("roomName")
    private String name;

    @JsonProperty("roomPrice")
    private Integer price;

    @JsonProperty("roomAmount")
    private Integer amount;

    @JsonProperty("roomMaxNum")
    private Integer peopleNum;


}
