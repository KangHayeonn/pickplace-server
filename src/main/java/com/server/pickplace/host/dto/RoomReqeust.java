package com.server.pickplace.host.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomReqeust {

    @NotBlank(message = "방 이름은 필수값입니다.")
    @JsonProperty("roomName")
    private String name;

    @Positive
    @NotNull(message = "가격은 필수값입니다.")
    @JsonProperty("roomPrice")
    private Integer price;

    @Positive
    @NotNull(message = "방 갯수는 필수값입니다.")
    @JsonProperty("roomAmount")
    private Integer amount;  // 방 갯수

    @JsonProperty("roomMaxNum")
    @NotNull(message = "최대 인원 수는 필수값입니다.")
    @Positive
    private Integer peopleNum;  // 최대 인원 수
}
