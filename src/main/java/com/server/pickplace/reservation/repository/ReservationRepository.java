package com.server.pickplace.reservation.repository;


import com.server.pickplace.member.entity.Member;
import com.server.pickplace.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {

    @Query("select m.name from Member m where m.email = :email")
    String findMemberNameByEmail(@Param("email") String email);

    @Query("select m.password from Member m where m.email = :email")
    String findMemberPasswordByEmail(@Param("email") String email);
}