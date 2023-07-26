package com.server.pickplace.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
public class CardPayRequest extends PayRequest {

    @NotBlank(message = "카드 비밀번호를 입력해주세요.")
    private String cardPassword;

    @Pattern(regexp = "^\\d{16}$", message = "올바른 형식의 카드번호를 입력해주세요.")
    private String cardNum;

    @Pattern(regexp = "^\\d{3}$", message = "올바른 형식의 CVC 번호를 입력해주세요.")
    private String cvc;


}
