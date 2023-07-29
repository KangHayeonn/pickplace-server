package com.server.pickplace.reservation.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class QRPayRequest extends PayRequest {

    @NotBlank(message = "올바른 QR결제 코드를 입력해주세요.")
    private String qrPaymentCode;


}
