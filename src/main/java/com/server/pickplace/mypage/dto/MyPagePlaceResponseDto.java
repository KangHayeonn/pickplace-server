package com.server.pickplace.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyPagePlaceResponseDto {
    Long reservationId;

    Long placeId;
}
