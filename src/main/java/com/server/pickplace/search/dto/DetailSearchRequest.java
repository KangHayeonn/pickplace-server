package com.server.pickplace.search.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.pickplace.place.entity.CategoryStatus;
import com.server.pickplace.place.entity.Tag;
import com.server.pickplace.place.entity.TagStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DetailSearchRequest extends NormalSearchRequest {


    @NotNull(message = "{category.NotNull}")
    private CategoryStatus category;

    @Positive
    @NotNull(message = "인원 수를 입력해주세요.")
    private Integer userCnt;

    @Positive
    @NotNull(message = "거리를 입력해주세요.")
    private Integer distance;

    private List<@NotNull(message = "{tag.NotNull}") TagStatus> tagList;


}
