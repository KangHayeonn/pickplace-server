package com.server.pickplace.review.error;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewException extends RuntimeException {

    private final ReviewErrorResult errorResult;

}
