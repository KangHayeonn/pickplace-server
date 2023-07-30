package com.server.pickplace.review.repository;

import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.review.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewRepositoryCustom {
    List<ReviewCategoryResponse> getReviewDtosById(Long id);

    PlaceReviewResponse getPlaceReviewDtoByPlaceId(Long placeId);

    List<ReviewResponse> getReviewResponseListByPlaceId(Long placeId);

    ReviewDetailResponse getReviewDetailDtoByReviewId(Long reviewId);

    void createReview(Reservation reservation, CreateReviewRequest createReviewRequest);

    @Transactional
    void updateReview(Long id, UpdateReviewRequest updateReviewRequest, Long reviewId);

    @Transactional
    void deleteReview(Long id, Long reviewId);
}
