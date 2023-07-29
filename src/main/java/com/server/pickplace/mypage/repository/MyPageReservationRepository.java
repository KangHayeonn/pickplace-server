package com.server.pickplace.mypage.repository;

import com.server.pickplace.mypage.dto.MyPageReservationResponseDto;
import com.server.pickplace.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MyPageReservationRepository extends JpaRepository<Reservation, Long> {

// ,MyPageReservationRepositoryCustom
//    @Query(value = "select r from Reservation r join r.room rm join rm.place p where p.id = :id and r.endDate >= :today")
//    Optional<List<Reservation>> findOptionalReservationListByPlaceId(@Param("id") Long id, @Param("today") LocalDate today);

//    @Query(value = "SELECT a FROM Reservation a JOIN a.member e JOIN a.room d JOIN d.place c WHERE e.id = :id AND d.place.id = c.id AND a.room.id = d.id")
//    Optional<List<Reservation>> findReservationsForMemberAndPlace2(@Param("id") Long id);

//    @Query("select r from Reservation r join r.member p where p.id = :id")

    @Query("select r from Reservation r join r.member p join r.room d join d.place c where p.id = :id")
    Optional<List<Reservation>> findReservationListByMemberId(@Param("id") Long id);

}
