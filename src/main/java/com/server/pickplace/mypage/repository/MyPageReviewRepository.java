package com.server.pickplace.mypage.repository;

import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyPageReviewRepository extends JpaRepository<Review, Long> {
    Optional<List<Review>> findReviewListByReservationId(Long id);
}
