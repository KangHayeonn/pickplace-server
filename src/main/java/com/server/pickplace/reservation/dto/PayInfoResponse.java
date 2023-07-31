package com.server.pickplace.reservation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PayInfoResponse {

    @JsonProperty("roomPrice")
    private Integer price;
}
