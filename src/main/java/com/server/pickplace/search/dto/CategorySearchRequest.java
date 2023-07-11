package com.server.pickplace.search.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class CategorySearchRequest extends SearchRequest {

    private Integer distance = 5;
    private Integer countPerPage = 10;


    @Positive
    private Long category;

    @Positive
    private Integer pageNum;

    @NotBlank
    private String searchType;

    private String address;

}
