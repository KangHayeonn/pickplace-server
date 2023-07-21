package com.server.pickplace.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
public class CardPayRequest extends PayRequest {

    @NotBlank
    private String cardPassword;

    @Pattern(regexp = "^\\d{16}$", message = "Invalid card number format")
    private String cardNum;

    @Pattern(regexp = "^\\d{3}$", message = "Invalid CVC format")
    private String cvc;


}
