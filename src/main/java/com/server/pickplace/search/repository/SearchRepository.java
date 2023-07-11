package com.server.pickplace.search.repository;

import com.server.pickplace.place.entity.Place;
import com.server.pickplace.place.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchRepository extends JpaRepository<Place, Long>, SearchRepositoryCustom {

    @Query("select r from Room r join r.place p where p.id = :placeId and r.id in :roomList")
    List<Room> findRoomsByList(@Param("roomList") List<Long> roomList, @Param("placeId") Long placeId);
}
