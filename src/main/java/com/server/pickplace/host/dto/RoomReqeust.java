package com.server.pickplace.host.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomReqeust {

    private String roomName;

    private Integer roomPrice;

    private Integer roomAmount;  // 방 갯수

    private Integer roomMaxNum;  // 최대 인원 수
}
