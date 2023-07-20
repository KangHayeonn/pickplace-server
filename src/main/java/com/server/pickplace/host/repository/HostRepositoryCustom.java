package com.server.pickplace.host.repository;

import com.server.pickplace.place.entity.*;

import java.util.List;

public interface HostRepositoryCustom {

    void savePlace(Place place);

    void saveRoom(Room room);


    Category findCategoryByCategoryStatus(CategoryStatus categoryStatus);

    void saveCategoryPlace(CategoryPlace categoryPlace);

    List<Tag> findTagListByTagStatusList(List<TagStatus> tagStatusList);

    void saveTagPlace(TagPlace build);
}
