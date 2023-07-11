package com.server.pickplace.search.repository;

import com.server.pickplace.search.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface SearchRepositoryCustom {

    Slice<PlaceResponse> findSliceByDto(CategorySearchRequest categorySearchRequest, Pageable pageable);

    Slice<PlaceResponse> findSliceByDto(BasicSearchRequest basicSearchRequest, Pageable pageable);

    Slice<PlaceResponse> findSliceByDto(DetailSearchRequest detailSearchRequest, Pageable pageable);

    List<Long> getUnableRoomList(DetailPageRequest detailPageRequest, Long placeId);
}
