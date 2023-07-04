package com.server.pickplace.host.repository;

import com.server.pickplace.place.entity.Place;
import com.server.pickplace.place.entity.Room;

public interface HostRepositoryCustom {

    void savePlace(Place place);

    void saveRoom(Room room);
}
