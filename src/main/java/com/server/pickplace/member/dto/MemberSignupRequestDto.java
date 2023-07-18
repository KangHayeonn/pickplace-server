package com.server.pickplace.member.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class MemberSignupRequestDto {

    @Email(message = "email 형식을 지켜주세요.")
    @NotBlank(message = "이메일은 필수 입력사항 입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력사항 입니다.")
    private String password;

    @NotBlank(message = "전화번호는 필수 입력사항 입니다.")
    private String phone;

    @Size(min=2, max=10) //길이 제한
    @NotBlank(message = "닉네임은 필수 입력사항 입니다.")
    private String nickname;

    @NotBlank(message = "역할은 필수 입력사항 입니다.")
    private String memberRole;
}

