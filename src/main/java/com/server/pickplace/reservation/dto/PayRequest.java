package com.server.pickplace.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class PayRequest {

    @Positive
    @NotNull(message = "올바른 방 번호를 입력해주세요.")
    private Long roomId;

    @NotNull(message = "{startDate.NotNull}")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 HH:mm")
    private LocalDateTime checkInTime;

    @NotNull(message = "{endDate.NotNull}")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 HH:mm")
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
