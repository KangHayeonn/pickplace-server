package com.server.pickplace.mypage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.pickplace.place.entity.CategoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyPageReservationResponseDto {

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

    CategoryStatus category;



    }

