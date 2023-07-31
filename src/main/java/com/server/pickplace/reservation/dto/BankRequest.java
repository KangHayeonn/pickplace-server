package com.server.pickplace.reservation.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class BankRequest {

    @NotBlank(message = "은행 이름을 입력해주세요.")
    private String bankName;

}
