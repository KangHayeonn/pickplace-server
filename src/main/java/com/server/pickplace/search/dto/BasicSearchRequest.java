package com.server.pickplace.search.dto;

import com.server.pickplace.place.entity.CategoryStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BasicSearchRequest extends NormalSearchRequest {

    private final Integer distance = 5000;
    private final Integer countPerPage = 10;

    @NotBlank  // Null, 빈 문자열, 스페이스만 있는 문자열 불가
    @Size(max = 255)
    private String address;
    private Double x;
    private Double y;

    private CategoryStatus category;

}
