package com.server.pickplace.search.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DetailPageRequest extends SearchRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @NotNull(message = "{startDate.NotNull}")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @NotNull(message = "{endDate.NotNull}")
    private LocalTime endTime;

}
