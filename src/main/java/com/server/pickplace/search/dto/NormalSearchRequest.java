package com.server.pickplace.search.dto;

import com.server.pickplace.config.validation.Enum;
import com.server.pickplace.search.entity.SearchType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;

@Getter
@SuperBuilder
@NoArgsConstructor
public class NormalSearchRequest extends SearchRequest {

    @NotBlank(message = "검색 타입을 입력해주세요.")
    @Enum(enumClass = SearchType.class, message = "올바른 검색 타입을 지정해주세요.")
    private String searchType;

    @PositiveOrZero
    @NotNull(message = "페이지 번호를 지정해주세요.")
    private Integer pageNum;

    @Positive
    @NotNull(message = "페이지 당 갯수를 지정해주세요.")
    private Integer countPerPage;

    @NotBlank(message = "{address.NotBlank}")
    @Size(max = 255)
    private String address;

    @Positive
    @NotNull(message = "{x.NotNull}")
    private Double x;

    @Positive
    @NotNull(message = "{y.NotNull}")
    private Double y;


}
