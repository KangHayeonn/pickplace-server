package com.server.pickplace.review.dto;


import com.querydsl.core.annotations.QueryProjection;

public class PlaceReviewResponse {

    private Long placeId;

    private String placeName;

    private Float placeRating;

    private Integer placeReviewCount;

    @QueryProjection
    public PlaceReviewResponse(Long placeId, String placeName, Float placeRating, Integer placeReviewCount) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeRating = placeRating / placeReviewCount;
        this.placeReviewCount = placeReviewCount;
    }
}
