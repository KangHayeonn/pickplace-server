package com.server.pickplace.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenInfo {
    //클라이언트에 보낼 토큰 dto
    private String accessToken;
    private String refreshToken;
}
