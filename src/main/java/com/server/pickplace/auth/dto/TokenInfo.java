package com.server.pickplace.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenInfo {
    //클라이언트에 토큰을 보내기 위한 dto
//    private String grantType; //jwt 인증타입 : bearer
    private String accessToken;
    private String refreshToken;
}
