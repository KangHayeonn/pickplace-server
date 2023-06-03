package com.server.pickplace.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDetailResponse {
    private Long id;
    private String userId;
    private String name;
}
