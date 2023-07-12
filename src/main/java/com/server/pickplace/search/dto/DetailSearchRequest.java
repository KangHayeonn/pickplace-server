package com.server.pickplace.search.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailSearchRequest extends NormalSearchRequest {

    private Integer countPerPage = 10;

    @NotBlank  // Null, 빈 문자열, 스페이스만 있는 문자열 불가
    @Size(max = 255)
    private String address;

    @Positive
    private Long category;

    @Positive
    private Integer userCnt;

    @Positive
    private Integer distance;

    private List<Long> tagId;

}
