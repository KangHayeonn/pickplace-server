package com.server.pickplace.search.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.pickplace.place.entity.CategoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySearchRequest extends NormalSearchRequest {

    private final Integer distance = 5000;

    @NotNull(message = "{category.NotNull}")
    private CategoryStatus category;



}
