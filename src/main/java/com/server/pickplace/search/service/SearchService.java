package com.server.pickplace.search.service;

import com.server.pickplace.search.dto.BasicSearchRequest;
import com.server.pickplace.search.dto.DetailSearchRequest;
import com.server.pickplace.search.dto.PlaceResponse;
import com.server.pickplace.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

    private final SearchRepository searchRepository;

    public Map<String, Object> findPlaceListByDto(BasicSearchRequest basicSearchRequest) {

        Map<String, Object> placeDtoHasNextMap = new HashMap<>();

        Integer pageNum = basicSearchRequest.getPageNum();
        Integer countPerPage = basicSearchRequest.getCountPerPage();

        PageRequest pageRequest = PageRequest.of(pageNum, countPerPage);

        Slice<PlaceResponse> sliceByDto = searchRepository.findSliceByDto(basicSearchRequest, pageRequest);

        placeDtoHasNextMap.put("placeList", sliceByDto.getContent());
        placeDtoHasNextMap.put("hasNext", sliceByDto.hasNext());

        return placeDtoHasNextMap;
    }

    public Map<String, Object> findPlaceListByDto(DetailSearchRequest detailSearchRequest) {

        Map<String, Object> placeDtoHasNextMap = new HashMap<>();

        Integer pageNum = detailSearchRequest.getPageNum();
        Integer countPerPage = detailSearchRequest.getCountPerPage();

        PageRequest pageRequest = PageRequest.of(pageNum, countPerPage);

        Slice<PlaceResponse> sliceByDto = searchRepository.findSliceByDto(detailSearchRequest, pageRequest);

        placeDtoHasNextMap.put("placeList", sliceByDto.getContent());
        placeDtoHasNextMap.put("hasNext", sliceByDto.hasNext());

        return placeDtoHasNextMap;

    }
}
