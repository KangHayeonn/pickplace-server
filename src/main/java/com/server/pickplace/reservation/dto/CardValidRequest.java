package com.server.pickplace.reservation.dto;

import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class CardValidRequest {

    @Pattern(regexp = "^\\d{16}$", message = "올바른 형식의 카드번호를 입력해주세요.")
    private String cardNum;

    @Pattern(regexp = "^\\d{3}$", message = "올바른 형식의 CVC 번호를 입력해주세요.")
    private String cvc;


}
