package com.server.pickplace.reservation.repository;


import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.search.repository.SearchRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {

}