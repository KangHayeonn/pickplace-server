package com.server.pickplace.review.repository;


import com.server.pickplace.reservation.repository.ReservationRepositoryCustom;
import com.server.pickplace.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
}
