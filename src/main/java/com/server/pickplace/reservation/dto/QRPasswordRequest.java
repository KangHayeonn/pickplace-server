package com.server.pickplace.reservation.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class QRPasswordRequest {

    @NotBlank(message = "비밀번호값을 입력해주세요.")
    private String qrPassword;

}
