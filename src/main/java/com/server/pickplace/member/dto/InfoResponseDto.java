package com.server.pickplace.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class InfoResponseDto {
    private String email;
    private String phone ;
    private String nickname ;
}
