package com.server.pickplace.reservation.dto;

import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class CardValidRequest {

    @Pattern(regexp = "^\\d{16}$", message = "Invalid card number format")
    private String cardNum;

    @Pattern(regexp = "^\\d{3}$", message = "Invalid CVC format")
    private String cvc;


}
