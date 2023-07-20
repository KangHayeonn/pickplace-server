package com.server.pickplace.host.dto;


import com.server.pickplace.place.entity.CategoryStatus;
import com.server.pickplace.place.entity.TagStatus;
import lombok.Getter;

import javax.validation.Valid;
import java.util.List;

@Getter
public class PlaceRoomReqeuest {

    @Valid
    private PlaceRequest place;

    @Valid
    private List<RoomReqeust> rooms;

    private CategoryStatus category;

    private List<TagStatus> tagList;

}
