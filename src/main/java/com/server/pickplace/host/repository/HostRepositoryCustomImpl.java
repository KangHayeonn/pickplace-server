package com.server.pickplace.host.repository;


import com.server.pickplace.place.entity.Place;
import com.server.pickplace.place.entity.Room;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
public class HostRepositoryCustomImpl implements HostRepositoryCustom {

    private final EntityManager em;

    @Override
    public void savePlace(Place place) {
        em.persist(place);
    }

    @Override
    public void saveRoom(Room room) {
        em.persist(room);
    }
}
