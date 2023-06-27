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

    @JsonFormat(pattern = "yyyy년 MM월 dd일 HH:mm")
    private LocalDateTime checkInTime;

    @JsonFormat(pattern = "yyyy년 MM월 dd일 HH:mm")
    private LocalDateTime checkOutTime;

    private ReservationStatus status;

    @JsonFormat(pattern = "yyyy년 MM월 dd일")
    private LocalDateTime createdDate;

    private Integer peopleNum;


}
