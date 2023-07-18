package com.server.pickplace.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ReissueResponseDto {
    private String accessToken;
    private String refreshToken;
}
