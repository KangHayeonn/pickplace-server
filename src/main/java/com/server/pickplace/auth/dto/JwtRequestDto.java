package com.server.pickplace.auth.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class JwtRequestDto {

    private String email;
    private String password;

}
