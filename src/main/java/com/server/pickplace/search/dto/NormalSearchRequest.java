package com.server.pickplace.search.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@SuperBuilder
@NoArgsConstructor
public class NormalSearchRequest extends SearchRequest {

    @NotBlank
    private String searchType;

    @PositiveOrZero
    private Integer pageNum;

    @Positive
    private Integer countPerPage;

}
