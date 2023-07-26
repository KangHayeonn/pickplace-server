package com.server.pickplace.review.repository;


import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    @Query("select r from Reservation r left join fetch r.review where r.id = :id")
    Optional<Reservation> findReservationByReservationId(@Param("id") Long reservationId);
}
