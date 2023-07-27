package com.server.pickplace.host.dto;

import com.server.pickplace.place.entity.CategoryStatus;
import com.server.pickplace.place.entity.TagStatus;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class PlaceUpdateRequest {

    @Valid
    private PlaceRequest place;

    @NotNull(message = "{category.NotNull}")
    private CategoryStatus category;

    private List<@NotNull(message = "{tag.NotNull}") TagStatus> tagList;

}
