package com.server.pickplace.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReviewDetailResponse {

    private Long reviewId;

    private String memberName;

    @JsonFormat(pattern = "yyyy년 MM월 dd일", locale = "ko-KR")
    private LocalDate reviewDate;

    private String placeAddress;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm")
    private LocalDateTime reservationDate;

    private Float reviewRating;

    private String reviewContent;

    @QueryProjection
    public ReviewDetailResponse(Long reviewId, String memberName, LocalDateTime reviewDate, String placeAddress, LocalDateTime reservationDate, Float reviewRating, String reviewContent) {
        this.reviewId = reviewId;
        this.memberName = memberName;
        this.reviewDate = reviewDate.toLocalDate();
        this.placeAddress = placeAddress;
        this.reservationDate = reservationDate;
        this.reviewRating = reviewRating;
        this.reviewContent = reviewContent;
    }
}
