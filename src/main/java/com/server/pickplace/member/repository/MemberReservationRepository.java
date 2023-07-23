package com.server.pickplace.member.repository;

import com.server.pickplace.member.entity.Member;
import com.server.pickplace.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface MemberReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(value = "select r from Reservation r join r.member p where p.id = :id")
    Optional<List<Reservation>> findReservationListByMemberId(@Param("id") Long id);

}
