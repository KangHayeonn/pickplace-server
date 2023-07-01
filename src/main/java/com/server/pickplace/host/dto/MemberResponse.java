package com.server.pickplace.host.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {

    @JsonProperty("memberName")
    private String name;
}
