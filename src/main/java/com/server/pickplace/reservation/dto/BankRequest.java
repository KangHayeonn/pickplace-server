package com.server.pickplace.reservation.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class BankRequest {

    @NotBlank
    private String bankName;

}
