package com.server.pickplace.review.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import com.server.pickplace.place.entity.CategoryStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ReviewCategoryResponse {

    private Long reviewId;

    private String placeName;

    private CategoryStatus placeCategory;

    private Long reservationId;

    private Float reviewRating;

    private String reviewContent;

    private String memberName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate reviewCreatedDate;


    @QueryProjection
    public ReviewCategoryResponse(Long reviewId, String placeName, CategoryStatus categoryStatus, Long reservationId, Float reviewRating, String reviewContent, String memberName, LocalDateTime reviewDate) {
        this.reviewId = reviewId;
        this.placeName = placeName;
        this.placeCategory = categoryStatus;
        this.reservationId = reservationId;
        this.reviewRating = reviewRating;
        this.reviewContent = reviewContent;
        this.memberName = memberName;
        this.reviewCreatedDate = reviewDate.toLocalDate();
    }
}
