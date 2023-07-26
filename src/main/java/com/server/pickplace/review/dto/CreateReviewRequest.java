package com.server.pickplace.review.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
public class CreateReviewRequest {

    @Positive
    @NotNull(message = "예약ID를 입력해주세요")
    private Long reservationId;

    @NotBlank
    @NotNull(message = "내용을 입력해주세요.")
    private String content;

    @Positive
    @NotNull(message = "별점을 입력해주세요.")
    private Float rating;

}
