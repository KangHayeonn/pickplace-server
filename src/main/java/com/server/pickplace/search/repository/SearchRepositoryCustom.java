package com.server.pickplace.search.repository;

import com.server.pickplace.search.dto.BasicSearchRequest;
import com.server.pickplace.search.dto.DetailSearchRequest;
import com.server.pickplace.search.dto.PlaceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface SearchRepositoryCustom {

    Slice<PlaceResponse> findSliceByDto(BasicSearchRequest basicSearchRequest, Pageable pageable);

    Slice<PlaceResponse> findSliceByDto(DetailSearchRequest detailSearchRequest, Pageable pageable);

}
