package com.server.pickplace.host.dto;


import lombok.Getter;

import javax.validation.Valid;
import java.util.List;

@Getter
public class PlaceRoomReqeuest {

    @Valid
    private PlaceRequest place;

    @Valid
    private List<RoomReqeust> rooms;

}
