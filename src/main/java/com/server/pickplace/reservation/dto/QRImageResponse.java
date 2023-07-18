package com.server.pickplace.reservation.dto;

import lombok.Builder;

@Builder
public class QRImageResponse {

    private byte[] qrImage;

    private String qrPaymentCode;
}
