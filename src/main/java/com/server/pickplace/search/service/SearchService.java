package com.server.pickplace.search.service;

import com.server.pickplace.place.entity.Place;
import com.server.pickplace.place.entity.Room;
import com.server.pickplace.search.dto.*;
import com.server.pickplace.search.error.SearchErrorResult;
import com.server.pickplace.search.error.SearchException;
import com.server.pickplace.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

    private final SearchRepository searchRepository;
    private final ModelMapper modelMapper;

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

    public Map<String, Object> getDetailPageMap(DetailPageRequest detailPageRequest, Long placeId) {

        Map<String, Object> detailPageMap = new HashMap<>();
        List<RoomResponse> roomResponseList = new ArrayList<>();

        Optional<Place> optionalPlace = searchRepository.findById(placeId);

        Place place = optionalPlace.orElseThrow(() -> new SearchException(SearchErrorResult.NOT_EXIST_PLACE));

        PlaceResponse placeResponse = getPlaceResponseByPlace(place);

        detailPageMap.put("place", placeResponse);

        Map<Long, Integer> roomUnitCountMap = searchRepository.getRoomUnitCountMap(detailPageRequest, placeId);

        Map<Long, Integer> unableRoomCountMap = searchRepository.getUnableRoomCountMap(detailPageRequest, placeId, new ArrayList<>(roomUnitCountMap.keySet()));

        List<Long> roomIdList = new ArrayList<>(roomUnitCountMap.keySet());
        List<Room> roomList = searchRepository.findRoomsByList(roomIdList, placeId);

        for (Room room : roomList) {

            RoomResponse roomResponse = modelMapper.map(room, RoomResponse.class);

            Long roomId = room.getId();
            Integer totalCount = roomUnitCountMap.get(roomId);
            Integer unableCount = unableRoomCountMap.get(roomId);


            if (totalCount > unableCount) {
                roomResponse.setStatus(true);
            } else {
                roomResponse.setStatus(false);
            }

            roomResponseList.add(roomResponse);

        }

        detailPageMap.put("roomList", roomResponseList);

        return detailPageMap;


    }

    private PlaceResponse getPlaceResponseByPlace(Place place) {
        PlaceResponse placeResponse = PlaceResponse.builder()
                .id(place.getId())
                .name(place.getName())
                .rating(place.getRating())
                .reviewCount(place.getReviewCount())
                .address(
                        new HashMap<>() {
                            {
                                put("address", place.getAddress());
                                put("latitude", place.getPoint().getX());
                                put("longitude", place.getPoint().getY());
                            }
                        }
                ).build();
        return placeResponse;
    }

    public void dateNullCheck(SearchRequest request) {

        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new SearchException(SearchErrorResult.DATE_TIME_NULL_CHECK);
        }
    }
}
