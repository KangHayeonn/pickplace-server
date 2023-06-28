package com.server.pickplace.host.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.server.pickplace.reservation.entity.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class ReservationResponse {

    @JsonProperty("reservationId")
    private Long id;

    @JsonProperty("reservationPeopleNum")
    private Integer peopleNum;

    @JsonProperty("reservationStatus")
    private ReservationStatus status;

    @JsonProperty("checkInDate")
    private LocalDate startDate;

    @JsonProperty("checkInTime")
    private LocalTime startTime;

    @JsonProperty("checkOutDate")
    private LocalDate endDate;

    @JsonProperty("checkOutTime")
    private LocalTime endTime;

    private String roomName;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;



}
