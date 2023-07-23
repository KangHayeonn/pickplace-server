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

    Long reservationId;

    Long placeId;

    String placeName;

    String startDate;

    String endDate;

    String startTime;

    String endTime;

    String reservationStatus;


    Boolean reviewExistence;

    @JsonProperty("reservationDate")
    String updateDate;



}
