package com.server.pickplace.host.repository;

import com.server.pickplace.host.dto.PlaceUpdateRequest;
import com.server.pickplace.host.dto.RoomReqeust;
import com.server.pickplace.place.entity.*;

import java.util.List;

public interface HostRepositoryCustom {

    Long savePlace(Place place);

    void saveRoom(Room room);

    Category findCategoryByCategoryStatus(CategoryStatus categoryStatus);

    void saveCategoryPlace(CategoryPlace categoryPlace);

    List<Tag> findTagListByTagStatusList(List<TagStatus> tagStatusList);

    void saveTagPlace(TagPlace build);

    void saveUnitByRoom(Room room);

    void updatePlaceByDto(Place place, Category category, List<Tag> tagList, PlaceUpdateRequest placeUpdateRequest);

    void deletePlace(Place place);

    void updateRoom(Room room, RoomReqeust roomReqeust);

    void deleteRoom(Room room);
}
