package com.server.pickplace.review.repository;

import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.review.dto.CreateReviewRequest;
import com.server.pickplace.review.dto.PlaceReviewResponse;
import com.server.pickplace.review.dto.ReviewDetailResponse;
import com.server.pickplace.review.dto.ReviewResponse;

import java.util.List;

public interface ReviewRepositoryCustom {
    List<ReviewResponse> getReviewDtosByEmail(String email);

    PlaceReviewResponse getPlaceReviewDtoByPlaceId(Long placeId);

    List<ReviewResponse> getReviewResponseListByPlaceId(Long placeId);

    ReviewDetailResponse getReviewDetailDtoByReviewId(Long reviewId);

    void createReview(Reservation reservation, CreateReviewRequest createReviewRequest);
}
