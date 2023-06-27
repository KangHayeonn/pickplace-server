package com.server.pickplace.host.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomReqeust {

    private String name;

    private Integer price;

    private Integer amount;  // 방 갯수

    private Integer maxNum;  // 최대 인원 수
}
