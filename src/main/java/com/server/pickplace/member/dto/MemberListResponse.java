package com.server.pickplace.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class MemberListResponse {
    private Long id;
    private String email;
    private String name;
}
