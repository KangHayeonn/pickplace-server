package com.server.pickplace.review.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
public class AmendReviewRequest {

    @NotBlank
    private String content;

    @Positive
    private Float rating;

}
