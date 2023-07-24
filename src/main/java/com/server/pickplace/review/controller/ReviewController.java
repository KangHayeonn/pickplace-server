package com.server.pickplace.review.controller;


import com.server.pickplace.common.dto.SingleResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.review.dto.UpdateReviewRequest;
import com.server.pickplace.review.dto.CreateReviewRequest;
import com.server.pickplace.review.dto.ReviewDetailResponse;
import com.server.pickplace.review.dto.ReviewResponse;
import com.server.pickplace.review.repository.ReviewRepository;
import com.server.pickplace.review.service.ReviewService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static java.util.Base64.getUrlDecoder;

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
    public ResponseEntity<SingleResponse<Map>> reviewPage(@RequestHeader("accessToken") String accessToken) {

        String email = reviewService.getPayloadMapAndGetEmail(accessToken);

        Map<String, List<ReviewResponse>> reviewListMap = reviewService.getReviewListMapByEmail(email);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), reviewListMap));

    }

    @ApiOperation(tags = "5. Review", value = "시설에 따른 리뷰 전체 조회", notes = "시설에 따른 리뷰 전체를 조회한다.")
    @GetMapping("/places/{placeId}")
    public ResponseEntity<SingleResponse<Map<String, Object>>> placeReviewPage(@PathVariable("placeId") Long placeId) {


        Map<String, Object> placeReviewListMap = reviewService.getPlaceReviewListMapByPlaceId(placeId);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), placeReviewListMap));

    }

    @ApiOperation(tags = "5. Review", value = "리뷰 상세 조회", notes = "reviewId를 수단으로, 리뷰를 상세 조회한다.")
    @GetMapping("{reviewId}")
    public ResponseEntity<SingleResponse<ReviewDetailResponse>> reviewDetailPage(@PathVariable("reviewId") Long reviewId) {

        ReviewDetailResponse reviewDetailResponse = reviewRepository.getReviewDetailDtoByReviewId(reviewId);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), reviewDetailResponse));
        
    }

    @ApiOperation(tags = "5. Review", value = "리뷰 생성", notes = "리뷰를 생성한다.")
    @PostMapping("")
    public ResponseEntity createReview(@RequestHeader("accessToken") String accessToken,
                             @Validated @RequestBody CreateReviewRequest createReviewRequest) {

        String email = reviewService.getPayloadMapAndGetEmail(accessToken);

        reviewService.createReviewByEmailAndRequest(email, createReviewRequest);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), null));
    }

    @ApiOperation(tags = "5. Review", value = "리뷰 변경", notes = "리뷰를 수정한다.")
    @PutMapping("/{reviewId}")
    public ResponseEntity updateReview(@RequestHeader("accessToken") String accessToken,
                                       @Validated @RequestBody UpdateReviewRequest updateReviewRequest,
                                       @PathVariable("reviewId") Long reviewId) {

        String email = reviewService.getPayloadMapAndGetEmail(accessToken);

        reviewRepository.updateReview(email, updateReviewRequest, reviewId);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), null));
    }

    @ApiOperation(tags = "5. Review", value = "리뷰 삭제", notes = "리뷰를 삭제한다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity deleteReview(@RequestHeader("accessToken") String accessToken,
                                      @PathVariable("reviewId") Long reviewId) {

        String email = reviewService.getPayloadMapAndGetEmail(accessToken);

        reviewRepository.deleteReview(email, reviewId);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), null));

    }













        private Map<String, Object> getPayloadMap(String accessToken) {

        String payloadJWT = accessToken.split("\\.")[1];
        Base64.Decoder decoder = getUrlDecoder();

        String payload = new String(decoder.decode(payloadJWT));
        JsonParser jsonParser = new BasicJsonParser();
        Map<String, Object> jsonArray = jsonParser.parseMap(payload);
        return jsonArray;
    }




}
