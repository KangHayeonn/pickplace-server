package com.server.pickplace.search.dto;

import com.server.pickplace.place.entity.CategoryStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder
@NoArgsConstructor
public class BasicSearchRequest extends NormalSearchRequest {

    private final Integer distance = 5000;

}
