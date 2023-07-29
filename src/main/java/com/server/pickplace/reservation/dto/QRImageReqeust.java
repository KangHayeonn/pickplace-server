package com.server.pickplace.reservation.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
public class QRImageReqeust {

    @Positive
    @NotNull(message = "방 가격을 지정해주세요.")
    private Integer roomPrice;

    @Positive
    @NotNull(message = "너비값을 지정해주세요.")
    private Integer width;

    @Positive
    @NotNull(message = "높이값을 지정해주세요.")
    private Integer height;


}
