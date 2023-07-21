package com.server.pickplace.reservation.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class QRPasswordRequest {

    @NotBlank
    private String qrPassword;

}
