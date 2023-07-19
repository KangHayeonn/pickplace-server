package com.server.pickplace.review.repository;

import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.review.dto.*;

import java.util.List;

public interface ReviewRepositoryCustom {
    List<ReviewResponse> getReviewDtosByEmail(String email);

    PlaceReviewResponse getPlaceReviewDtoByPlaceId(Long placeId);

    List<ReviewResponse> getReviewResponseListByPlaceId(Long placeId);

    ReviewDetailResponse getReviewDetailDtoByReviewId(Long reviewId);

    void createReview(Reservation reservation, CreateReviewRequest createReviewRequest);

    void amendReview(String email, AmendReviewRequest amendReviewRequest, Long reviewId);

    void deleteReview(String email, Long reviewId);
}
