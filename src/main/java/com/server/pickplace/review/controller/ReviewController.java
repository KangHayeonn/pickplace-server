package com.server.pickplace.review.controller;


import com.server.pickplace.common.dto.SingleResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.review.dto.MemberReviewResponse;
import com.server.pickplace.review.repository.ReviewRepository;
import com.server.pickplace.review.service.ReviewService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
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
    @GetMapping("/members/{memberID}")
    public ResponseEntity<SingleResponse<Map>> reservationPage(@RequestHeader("Authorization") String accessToken) {

        Map<String, Object> payloadMap = getPayloadMap(accessToken); // 일단 토큰이 존재하고, 유효하다고 가정
        String email = (String) payloadMap.get("email");


        List<MemberReviewResponse> memberReviewResponses =  reviewRepository.getMemberReviewDtosByEmail(email);

        Map<String, Object> reviewListMap = new HashMap<>();
        reviewListMap.put("reviewList", memberReviewResponses);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), reviewListMap));

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
