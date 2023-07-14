package com.server.pickplace.member.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Data
public class JwtRequestDto {

    @NotBlank(message = "공백")
    private String email;

    @NotBlank(message = "공백")
    private String password;

}
