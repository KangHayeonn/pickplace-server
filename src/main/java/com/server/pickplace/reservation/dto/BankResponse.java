package com.server.pickplace.reservation.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BankResponse {

    private String bankName;

    private String bankNum;

}
