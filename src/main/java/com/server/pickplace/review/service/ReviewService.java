package com.server.pickplace.review.service;


import com.server.pickplace.common.service.CommonService;
import com.server.pickplace.reservation.entity.Reservation;
import com.server.pickplace.review.dto.CreateReviewRequest;
import com.server.pickplace.review.dto.PlaceReviewResponse;
import com.server.pickplace.review.dto.ReviewCategoryResponse;
import com.server.pickplace.review.dto.ReviewResponse;
import com.server.pickplace.review.error.ReviewErrorResult;
import com.server.pickplace.review.error.ReviewException;
import com.server.pickplace.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService extends CommonService {

    private final ReviewRepository reviewRepository;

    public Map<String, Object> getPlaceReviewListMapByPlaceId(Long placeId) {
        Map<String, Object> placeReviewListMap = new HashMap<>();

        PlaceReviewResponse placeReviewResponse = reviewRepository.getPlaceReviewDtoByPlaceId(placeId);

        List<ReviewResponse> reviewResponses = reviewRepository.getReviewResponseListByPlaceId(placeId);

        placeReviewListMap.put("place", placeReviewResponse);
        placeReviewListMap.put("reviewList", reviewResponses);

        return placeReviewListMap;
    }

    public void createReviewByEmailAndRequest(Long id, CreateReviewRequest createReviewRequest) {

        // 1. reservation 존재 + 본인꺼 + 아직 리뷰 없음
        Optional<Reservation> optionalReservation = reviewRepository.findReservationByReservationId(createReviewRequest.getReservationId());
        Reservation reservation = optionalReservation.orElseThrow(() -> new ReviewException(ReviewErrorResult.WRONG_RESERVATION_ID));
        if (!(reservation.getMember().getId().equals(id))) {
            throw new ReviewException(ReviewErrorResult.NO_PERMISSION);
        } else if (!(reservation.getReview() == null)) {
            throw new ReviewException(ReviewErrorResult.REVIEW_ALREADY_EXIST);
        }

        // 2. 리뷰 등록
        reviewRepository.createReview(reservation, createReviewRequest);

    }


    public Map<String, List<ReviewCategoryResponse>> getReviewListMapById(Long id) {

        List<ReviewCategoryResponse> reviewResponse =  reviewRepository.getReviewDtosById(id);

        Map<String, List<ReviewCategoryResponse>> reviewListMap = new HashMap<>();
        reviewListMap.put("reviewList", reviewResponse);

        return reviewListMap;
    }
}
