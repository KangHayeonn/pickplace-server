package com.server.pickplace.search.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.pickplace.place.entity.CategoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySearchRequest extends NormalSearchRequest {

    private final Integer distance = 5000;
    private final Integer countPerPage = 10;

    private CategoryStatus category;


    @NotBlank  // Null, 빈 문자열, 스페이스만 있는 문자열 불가
    @Size(max = 255)
    private String address;

    @Positive
    private Double x;
    @Positive
    private Double y;

}
