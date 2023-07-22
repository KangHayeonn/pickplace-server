package com.server.pickplace.search.controller;

import com.server.pickplace.common.dto.SingleResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.search.dto.*;
import com.server.pickplace.search.service.SearchService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/search")
@Slf4j
public class SearchController {

    private final ResponseService responseService;
    private final SearchService searchService;

    @ApiOperation(tags = "3. Search", value = "상세페이지", notes = "상세페이지에서의 날짜/시간 조건에 따른 이용가능한 Room 검색")
    @PostMapping("/{placeId}")
    public ResponseEntity<SingleResponse<Map>> detailPage(@Validated @RequestBody DetailPageRequest detailPageRequest,
                                                          @PathVariable Long placeId) {

        Map<String, Object> detailPageMap = searchService.getDetailPageMap(detailPageRequest, placeId);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), detailPageMap));

    }


    @ApiOperation(tags = "3. Search", value = "카테고리 검색", notes = "카테고리 만을 활용한 검색(메인페이지 에서의 이동)")
    @PostMapping("/category")
    public ResponseEntity<SingleResponse<Map>> categorySearch(@Validated @RequestBody CategorySearchRequest request) {

        CategorySearchRequest categorySearchRequest = CategorySearchRequest.builder()
                .address(request.getAddress())
                .x(request.getX())
                .y(request.getY())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .searchType(request.getSearchType())
                .countPerPage(request.getCountPerPage())
                .pageNum(request.getPageNum())
                .category(request.getCategory())
                .countPerPage(request.getCountPerPage())
                .build();


        Map<String, Object> placeDtoHasNextMap = searchService.findPlaceListByDto(categorySearchRequest);
        placeDtoHasNextMap.put("countPerPage", request.getCountPerPage());


        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), placeDtoHasNextMap));

    }


    @ApiOperation(tags = "3. Search", value = "일반 검색", notes = "주소/날짜/기본거리(5km)/정렬순 을 활용한 기본 검색")
    @PostMapping("/basic")
    public ResponseEntity<SingleResponse<Map>> basicSearch(@Validated @RequestBody BasicSearchRequest basicSearchRequest) {

        Map<String, Object> placeDtoHasNextMap = searchService.findPlaceListByDto(basicSearchRequest);
        placeDtoHasNextMap.put("countPerPage", basicSearchRequest.getCountPerPage());


        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), placeDtoHasNextMap));

    }

    @ApiOperation(tags = "3. Search", value = "상세 검색", notes = "주소/날짜와시간/카테고리/인원수/거리/태그/정렬순 을 활용한 상세 검색")
    @PostMapping("/detail")
    public ResponseEntity<SingleResponse<Map>> placePage(@Validated @RequestBody DetailSearchRequest detailSearchRequest) {

        Map<String, Object> placeDtoHasNextMap = searchService.findPlaceListByDto(detailSearchRequest);
        placeDtoHasNextMap.put("countPerPage", detailSearchRequest.getCountPerPage());

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), placeDtoHasNextMap));

    }
}


