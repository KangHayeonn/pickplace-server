package com.server.pickplace.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class PayRequest {

    @Positive
    private Long roomId;

    @JsonFormat(pattern = "yyyy년 MM월 dd일(EEE) HH:mm", locale = "ko-KR")
    private LocalDateTime checkInTime;

    @JsonFormat(pattern = "yyyy년 MM월 dd일(EEE) HH:mm", locale = "ko-KR")
    private LocalDateTime checkOutTime;

    public LocalDate getStartDate() {
        return checkInTime.toLocalDate();
    }

    public LocalDate getEndDate() {
        return checkOutTime.toLocalDate();
    }

    public LocalTime getStartTime() {
        return checkInTime.toLocalTime();
    }

    public LocalTime getEndTime() {
        return checkOutTime.toLocalTime();
    }


}
