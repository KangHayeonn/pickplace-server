package com.server.pickplace.search.repository;

import com.server.pickplace.search.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;

public interface SearchRepositoryCustom {

    Slice<PlaceResponse> findSliceByDto(CategorySearchRequest categorySearchRequest, Pageable pageable);

    Slice<PlaceResponse> findSliceByDto(BasicSearchRequest basicSearchRequest, Pageable pageable);

    Slice<PlaceResponse> findSliceByDto(DetailSearchRequest detailSearchRequest, Pageable pageable);

    Map<Long, Integer> getUnableRoomCountMap(DetailPageRequest detailPageRequest, Long placeId, List<Long> roomIdList);

    Map<Long, Integer> getRoomUnitCountMap(DetailPageRequest detailPageRequest, Long placeId);
}
