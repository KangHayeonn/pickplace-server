package com.server.pickplace.member.dto.mypageDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class MemberReservationResponseDto {
    Long reservationId;
    Long placeId;
    String placeName;
    String startDate;
    String endDate;
    String startTime;
    String endTime;
    String reservationStatus;
    Boolean reviewExistence;

}
