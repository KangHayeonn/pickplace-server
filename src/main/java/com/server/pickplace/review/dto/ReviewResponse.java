package com.server.pickplace.review.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReviewResponse {

    private Long reviewId;

    private String placeName;

    private Long reservationId;

    private Float reviewRating;

    private String reviewContent;

    private String memberName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate reviewCreatedDate;


    @QueryProjection
    public ReviewResponse(Long reviewId, String placeName, Long reservationId, Float reviewRating, String reviewContent, String memberName, LocalDateTime reviewDate) {
        this.reviewId = reviewId;
        this.placeName = placeName;
        this.reservationId = reservationId;
        this.reviewRating = reviewRating;
        this.reviewContent = reviewContent;
        this.memberName = memberName;
        this.reviewCreatedDate = reviewDate.toLocalDate();
    }
}
