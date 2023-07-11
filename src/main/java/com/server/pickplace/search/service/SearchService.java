package com.server.pickplace.search.service;

import com.server.pickplace.place.entity.Place;
import com.server.pickplace.search.dto.*;
import com.server.pickplace.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

    private final SearchRepository searchRepository;

    public Map<String, Object> findPlaceListByDto(CategorySearchRequest categorySearchRequest) {

        Map<String, Object> placeDtoHasNextMap = new HashMap<>();

        Integer pageNum = categorySearchRequest.getPageNum();
        Integer countPerPage = categorySearchRequest.getCountPerPage();

        PageRequest pageRequest = PageRequest.of(pageNum, countPerPage);

        Slice<PlaceResponse> sliceByDto = searchRepository.findSliceByDto(categorySearchRequest, pageRequest);

        placeDtoHasNextMap.put("placeList", sliceByDto.getContent());
        placeDtoHasNextMap.put("hasNext", sliceByDto.hasNext());

        return placeDtoHasNextMap;
    }


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

    public void getDetailPageMap(DetailPageRequest detailPageRequest, Long placeId) {

        Optional<Place> optionalPlace = searchRepository.findById(placeId);
        Place place = optionalPlace.get();  // 추후 수정

        searchRepository.getUnableRoomList(detailPageRequest, placeId);

    }
}
