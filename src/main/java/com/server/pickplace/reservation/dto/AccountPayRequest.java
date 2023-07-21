package com.server.pickplace.reservation.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;


@Getter
public class AccountPayRequest extends PayRequest {

    @NotBlank
    private String bankName;

    @NotBlank
    private String bankNum;

    @NotBlank
    private String accountPassword;

}
