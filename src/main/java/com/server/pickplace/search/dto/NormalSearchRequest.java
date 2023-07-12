package com.server.pickplace.search.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@SuperBuilder
@NoArgsConstructor
public class NormalSearchRequest extends SearchRequest {

    @NotBlank
    private String searchType;

    @Positive
    private Integer pageNum;

}
