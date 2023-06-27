package com.server.pickplace.host.dto;


import lombok.Getter;

import java.util.List;

@Getter
public class PlaceRoomReqeuest {

    private PlaceRequest place;

    private List<RoomReqeust> rooms;

}
