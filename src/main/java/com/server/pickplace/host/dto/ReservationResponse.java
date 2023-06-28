package com.server.pickplace.host.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.server.pickplace.reservation.entity.ReservationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ReservationResponse {

    private Long id; // 예약번호

    private String roomName;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private ReservationStatus status;

    private LocalDateTime createdDate;

    private Integer peopleNum;


}
