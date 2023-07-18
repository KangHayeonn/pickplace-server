package com.server.pickplace.member.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
//@RequiredArgsConstructor
public class EmailCheckRequestDto {

    @NotBlank(message = "공백")
    @Email(message = "형식")
    String email;
}
