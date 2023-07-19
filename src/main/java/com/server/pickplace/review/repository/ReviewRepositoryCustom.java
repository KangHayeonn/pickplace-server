package com.server.pickplace.review.repository;

import com.server.pickplace.review.dto.MemberReviewResponse;

import java.util.List;

public interface ReviewRepositoryCustom {
    List<MemberReviewResponse> getMemberReviewDtosByEmail(String email);
}
