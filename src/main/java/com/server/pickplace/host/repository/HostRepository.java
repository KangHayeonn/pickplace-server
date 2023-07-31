package com.server.pickplace.host.repository;


import com.server.pickplace.member.entity.Member;
import com.server.pickplace.place.entity.Place;
import com.server.pickplace.place.entity.Room;
import com.server.pickplace.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HostRepository extends JpaRepository<Member, Long>, HostRepositoryCustom {

    @Query(value = "select p from Place p join p.member m where m.id = :id")
    Optional<List<Place>> findPlaceListById(@Param("id") Long id);

    @Query(value = "select r from Room r join r.place p where p.id = :id")
    Optional<List<Room>> findOptionalRoomListByPlaceId(@Param("id") Long id);

    @Query(value = "select p from Place p join fetch p.member where p.id = :id")
    Optional<Place> findOptionalPlaceByPlaceId(@Param("id") Long id);

    @Query(value = "select r from Reservation r join r.room rm join rm.place p where p.id = :id")
    Optional<List<Reservation>> findOptionalReservationListByPlaceId(@Param("id") Long id);

    @Query(value = "select r, p from Reservation r join r.room rm join rm.place p join p.member m where m.id = :id")
    Optional<List<Object[]>> findOptionalReservationAndNamesById(@Param("id") Long id);

    // 예약, 고객, 플레이스
    @Query(value = "select m, r, p from Reservation r join r.room rm join rm.place p join p.member m where r.id = :id")
    Optional<List<Object[]>> findOptionalMemberReservationPlaceListByReservationId(@Param("id") Long reservationId);

    Member findByEmail(String email);

    @Query(value = "select p from Place p join fetch p.member where p.id = :id")
    Optional<Place> findOptionalPlaceByPlaceIdFetchCategoryAndTag(@Param("id") Long id);

    @Query(value = "select r from Room r join fetch r.place p join fetch p.member m where r.id = :id")
    Optional<Room> findRoomByRoomId(@Param("id") Long roomId);

}
