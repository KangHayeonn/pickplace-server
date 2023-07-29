package com.server.pickplace.review.repository;

import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.review.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewRepositoryCustom {
    List<ReviewResponse> getReviewDtosByEmail(String email);

    PlaceReviewResponse getPlaceReviewDtoByPlaceId(Long placeId);

    List<ReviewResponse> getReviewResponseListByPlaceId(Long placeId);

    ReviewDetailResponse getReviewDetailDtoByReviewId(Long reviewId);

    void createReview(Reservation reservation, CreateReviewRequest createReviewRequest);

    @Transactional
    void updateReview(String email, UpdateReviewRequest updateReviewRequest, Long reviewId);

    @Transactional
    void deleteReview(String email, Long reviewId);
}
