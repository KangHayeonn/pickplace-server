package com.server.pickplace.member.dto;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@RequiredArgsConstructor
public class EmailCheckRequestDto {
    @Email(message = "email 형식을 지켜주세요.")
    String email;
}
