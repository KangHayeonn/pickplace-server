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
    @Query("select r from Reservation r join r.member p join r.room d join d.place c where p.id = :id")
    Optional<List<Reservation>> findReservationListByMemberId(@Param("id") Long id);

    Optional<List<Reservation>> findReservationListById(Long id);


}
