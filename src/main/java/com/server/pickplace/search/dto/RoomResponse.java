package com.server.pickplace.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class RoomResponse {

    @JsonProperty("roomId")
    private Long id;

    @JsonProperty("roomName")
    private String name;

    @JsonProperty("roomPrice")
    private Integer price;

    @JsonProperty("roomMaxNum")
    private Integer peopleNum;


    private String status;  // 예약 가능 vs 불가능
}
