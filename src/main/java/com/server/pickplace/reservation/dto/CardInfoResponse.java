package com.server.pickplace.reservation.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CardInfoResponse {


    @JsonProperty("memberName")
    private String name;

    @JsonProperty("cardNumber")
    private String number;


}
