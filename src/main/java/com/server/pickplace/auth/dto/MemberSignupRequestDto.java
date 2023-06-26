package com.server.pickplace.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignupRequestDto {

    private String email;
    private String password;
    private String name;
    private String number;
    private String nickname;
//    private String memberRole;

}