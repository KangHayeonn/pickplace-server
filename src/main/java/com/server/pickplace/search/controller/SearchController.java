package com.server.pickplace.search.controller;

import com.server.pickplace.common.dto.SingleResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.search.dto.BasicSearchRequest;
import com.server.pickplace.search.dto.DetailSearchRequest;
import com.server.pickplace.search.repository.SearchRepository;
import com.server.pickplace.search.service.SearchService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "3. Search", description = "SEARCH API")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/search")
@Slf4j
public class SearchController {

    private final ResponseService responseService;
    private final SearchService searchService;
    private final SearchRepository searchRepository;


    @ApiOperation(tags = "3. Search", value = "일반 검색", notes = "주소/날짜/기본거리(5km)/정렬순 을 활용한 기본 검색")
    @GetMapping("/basic")
    public ResponseEntity<SingleResponse<Map>> basicSearch(@Validated @RequestBody BasicSearchRequest basicSearchRequest) {

        Map<String, Object> placeDtoHasNextMap = searchService.findPlaceListByDto(basicSearchRequest);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), placeDtoHasNextMap));

    }

    @ApiOperation(tags = "3. Search", value = "상세 검색", notes = "주소/날짜와시간/카테고리/인원수/거리/태그/정렬순 을 활용한 상세 검색")
    @GetMapping("/detail")
    public ResponseEntity<SingleResponse<Map>> placePage(@Validated @RequestBody DetailSearchRequest detailSearchRequest) {

        Map<String, Object> placeDtoHasNextMap = searchService.findPlaceListByDto(detailSearchRequest);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), placeDtoHasNextMap));

    }
}


