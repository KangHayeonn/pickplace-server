package com.server.pickplace.review.controller;


import com.server.pickplace.common.dto.SingleResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.review.dto.*;
import com.server.pickplace.review.repository.ReviewRepository;
import com.server.pickplace.review.service.ReviewService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/review")
@Slf4j
public class ReviewController {

    private final ResponseService responseService;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    @ApiOperation(tags = "5. Review", value = "작성한 리뷰 전체 조회", notes = "사용자 입장에서, 작성한 리뷰 전체를 조회한다.")
    @GetMapping("")
    public ResponseEntity<SingleResponse<Map>> reviewPage(@RequestParam("memberId") Long id) {


        Map<String, List<ReviewCategoryResponse>> reviewListMap = reviewService.getReviewListMapById(id);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), reviewListMap));

    }

    @ApiOperation(tags = "5. Review", value = "시설에 따른 리뷰 전체 조회", notes = "시설에 따른 리뷰 전체를 조회한다.")
    @GetMapping("/places/{placeId}")
    public ResponseEntity<SingleResponse<Map<String, Object>>> placeReviewPage(@PathVariable("placeId") Long placeId) {


        Map<String, Object> placeReviewListMap = reviewService.getPlaceReviewListMapByPlaceId(placeId);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), placeReviewListMap));

    }

    @ApiOperation(tags = "5. Review", value = "리뷰 상세 조회", notes = "reviewId를 수단으로, 리뷰를 상세 조회한다.")
    @GetMapping("/detail/{reviewId}")
    public ResponseEntity<SingleResponse<ReviewDetailResponse>> reviewDetailPage(@PathVariable("reviewId") Long reviewId) {

        ReviewDetailResponse reviewDetailResponse = reviewRepository.getReviewDetailDtoByReviewId(reviewId);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), reviewDetailResponse));
        
    }

    @ApiOperation(tags = "5. Review", value = "리뷰 생성", notes = "리뷰를 생성한다.")
    @PostMapping("")
    public ResponseEntity createReview(@RequestParam("memberId") Long id,
                                       @RequestBody @Validated CreateReviewRequest createReviewRequest) {


        reviewService.createReviewByEmailAndRequest(id, createReviewRequest);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), new Object()));
    }

    @ApiOperation(tags = "5. Review", value = "리뷰 변경", notes = "리뷰를 수정한다.")
    @PutMapping("/{reviewId}")
    public ResponseEntity updateReview(@RequestParam("memberId") Long id,
                                       @RequestBody @Validated UpdateReviewRequest updateReviewRequest,
                                       @PathVariable("reviewId") Long reviewId) {

        reviewRepository.updateReview(id, updateReviewRequest, reviewId);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), new Object()));
    }

    @ApiOperation(tags = "5. Review", value = "리뷰 삭제", notes = "리뷰를 삭제한다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity deleteReview(@RequestParam("memberId") Long id,
                                      @PathVariable("reviewId") Long reviewId) {

        reviewRepository.deleteReview(id, reviewId);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), new Object()));

    }



}
