package com.server.pickplace.search.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BasicSearchRequest extends NormalSearchRequest {

    private Integer distance = 5000;
    private Integer countPerPage = 10;


    @NotBlank  // Null, 빈 문자열, 스페이스만 있는 문자열 불가
    @Size(max = 255)
    private String address;

    private Long category;

}
