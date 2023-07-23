package com.server.pickplace.member.repository;

import com.server.pickplace.member.entity.Member;
import com.server.pickplace.reservation.entity.Reservation;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MemberReservationRepository extends JpaRepository<Reservation, Long> {
//    Optional<List<Object[]>> findByMember(final Long id);

    List<Reservation> findByMember_Id(Long id);
}
