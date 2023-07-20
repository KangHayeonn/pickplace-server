package com.server.pickplace.host.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomReqeust {

    @NotBlank
    @JsonProperty("roomName")
    private String name;

    @Positive
    @JsonProperty("roomPrice")
    private Integer price;

    @Positive
    @JsonProperty("roomAmount")
    private Integer amount;  // 방 갯수

    @JsonProperty("roomMaxNum")
    @Positive
    private Integer peopleNum;  // 최대 인원 수
}
