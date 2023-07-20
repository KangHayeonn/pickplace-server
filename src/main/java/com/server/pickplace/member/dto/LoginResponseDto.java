package com.server.pickplace.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class LoginResponseDto {
        private Long memberId;
        private String nickname;
        private String accessToken;
        private String refreshToken;
}
