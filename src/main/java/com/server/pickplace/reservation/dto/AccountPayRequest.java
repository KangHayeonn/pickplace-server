package com.server.pickplace.reservation.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;


@Getter
public class AccountPayRequest extends PayRequest {

    @NotBlank(message = "은행 이름을 입력해주세요.")
    private String bankName;

    @NotBlank(message = "계좌번호를 입력해주세요.")
    private String bankNum;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String accountPassword;

}
