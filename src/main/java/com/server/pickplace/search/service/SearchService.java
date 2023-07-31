package com.server.pickplace.search.service;

import com.server.pickplace.place.entity.*;
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

    public void dateNullCheck(SearchRequest request) {

        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new SearchException(SearchErrorResult.DATE_TIME_NULL_CHECK);
        }
    }

    public void timeNullCheck(DetailPageRequest request, Long placeId) {

        // 1. 플레이스 카테고리 가져오기
        CategoryStatus categoryStatus = searchRepository.findCategoryStatusByPlaceId(placeId);

        // 2.1. 카테고리가 호텔/리조트, 펜션, 게하면 -> 시간 값이 null 이어야함
        // 2.2. 카테고리가 스터디룸, 파티룸이면 -> 시간 값이 null 아니어야 함

        List<CategoryStatus> categoryStatusList = new ArrayList<>(Arrays.asList(CategoryStatus.Hotel, CategoryStatus.Pension, CategoryStatus.GuestHouse));

        if (categoryStatusList.contains(categoryStatus)) { // 싱글톤 보장

            if (request.getStartTime() != null || request.getEndTime() != null) {
                throw new SearchException(SearchErrorResult.TIME_CONDITION_CHECK);
            }

        } else {

            if (request.getStartTime() == null || request.getEndTime() == null) {
                throw new SearchException(SearchErrorResult.TIME_CONDITION_CHECK);
            }

        }


    }


    private PlaceResponse getPlaceResponseByPlace(Place place) {

        List<TagStatus> tagStatusList = getTagStatusList(place);

        PlaceResponse placeResponse = PlaceResponse.builder()
                .id(place.getId())
                .name(place.getName())
                .rating(place.getReviewCount().equals(0) ? 0 : place.getRating() / place.getReviewCount())
                .reviewCount(place.getReviewCount())
                .address(
                        new HashMap<>() {
                            {
                                put("address", place.getAddress());
                                put("latitude", place.getPoint().getX());
                                put("longitude", place.getPoint().getY());
                            }
                        }
                )
                .categoryStatus(place.getCategories().get(0).getCategory().getStatus())
                .tagStatusList(tagStatusList)
                .build();

        return placeResponse;
    }

    private List<TagStatus> getTagStatusList(Place place) {
        List<TagStatus> tagStatusList = new ArrayList<>();
        for (TagPlace tagPlace : place.getTags()) {
            TagStatus tagStatus = tagPlace.getTag().getTagStatus();
            tagStatusList.add(tagStatus);
        }
        return tagStatusList;
    }




}
