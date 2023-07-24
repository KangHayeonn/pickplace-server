package com.server.pickplace.member.dto.mypageDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRoomDto {
    Long placeId;
    String placeName;
}
