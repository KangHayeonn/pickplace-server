package com.server.pickplace.mailling.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PassWordEditDto {
    Long memberId;
    String password ;
}
