package com.server.pickplace.member.dto.mypageDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.pickplace.reservation.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberReservationResponseDto {

    @JsonProperty("reservationId")
    Long reservationId;

    Long placeId;

    String placeName;

    @JsonProperty("checkInDate")
    String startDate;

    @JsonProperty("checkOutDate")
    String endDate;

    @JsonProperty("checkInDate")
    String startTime;

    @JsonProperty("checkOutTime")
    String endTime;

    @JsonProperty("reservationStatus")
    String reservationStatus;

    Boolean reviewExistence;




}
