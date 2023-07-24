package com.server.pickplace.member.repository;

import com.server.pickplace.place.entity.Room;
import com.server.pickplace.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRoomRepository extends JpaRepository<Room, Long> {

    @Query(value = "select r from Room join r.place p where p.id = :id ")
    Optional<List<Room>> findPlaceListByMemberId(@Param("id") Long id); //roomId 로 placeId 및 place 명 찾기 위함
}
