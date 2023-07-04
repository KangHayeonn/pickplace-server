package com.server.pickplace.host.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class MemberResponse {

    @JsonProperty("memberName")
    private String name;
}
