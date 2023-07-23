package com.server.pickplace.reservation.dto;

import lombok.Getter;

import javax.validation.constraints.Positive;

@Getter
public class QRImageReqeust {

    @Positive
    private Integer roomPrice;

    @Positive
    private Integer width;

    @Positive
    private Integer height;


}
