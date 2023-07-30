package com.server.pickplace.reservation.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QRImageResponse {

    private byte[] qrImage;

    private String qrPaymentCode;
}
