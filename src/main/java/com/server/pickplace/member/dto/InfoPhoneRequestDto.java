package com.server.pickplace.member.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class InfoPhoneRequestDto {
    Long memberId;
    String phone;
}
