package com.server.pickplace.reservation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberInfoResponse {

    @JsonProperty("memberName")
    private String name;

    @JsonProperty("memberPhone")
    private String number;

}
