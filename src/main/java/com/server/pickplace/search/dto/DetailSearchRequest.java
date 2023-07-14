package com.server.pickplace.search.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.pickplace.place.entity.CategoryStatus;
import com.server.pickplace.place.entity.Tag;
import com.server.pickplace.place.entity.TagStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DetailSearchRequest extends NormalSearchRequest {

    private Integer countPerPage = 10;

    @NotBlank  // Null, 빈 문자열, 스페이스만 있는 문자열 불가
    @Size(max = 255)
    private String address;
    private Double x;
    private Double y;

    private CategoryStatus category;

    @Positive
    private Integer userCnt;

    @Positive
    private Integer distance;

    private List<TagStatus> tagList;


}
