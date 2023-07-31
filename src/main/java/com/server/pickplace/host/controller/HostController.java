package com.server.pickplace.host.controller;


import com.server.pickplace.common.dto.ListResponse;
import com.server.pickplace.common.dto.SingleResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.host.dto.*;
import com.server.pickplace.host.service.HostService;
import com.server.pickplace.member.entity.Member;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/host")
@Slf4j
public class HostController {

    private final ResponseService responseService;
    private final HostService hostService;

    @ApiOperation(tags = "2. Host", value = "공간 관리 페이지", notes = "DB상에 존재하는 플레이스들을 보여준다.")
    @GetMapping("/place")
    public ResponseEntity<ListResponse<PlaceResponse>> placePage(@RequestParam("memberId") Long id) {

        hostService.hostCheck(id);
        
        List<PlaceResponse> placeList = hostService.findPlaceDtoListById(id);

        return ResponseEntity.ok(responseService.getListResponse(HttpStatus.OK.value(), placeList));

    }

    @ApiOperation(tags = "2. Host", value = "공간 상세 - 방 조회", notes = "시설 내의 방 리스트를 보여준다.")
    @GetMapping("/{placeId}/rooms")
    public ResponseEntity<SingleResponse<Map>> roomPage(@RequestParam("memberId") Long id,
                                                        @PathVariable Long placeId) {

        hostService.hostCheck(id);

        PlaceResponse placeDto = hostService.findPlaceDtoByPlaceId(id, placeId);

        List<RoomInfoResponse> roomDtos = hostService.findRoomDtoListByPlaceId(placeId);

        Map<String, Object> roomPlaceDtos = new HashMap<>();
        roomPlaceDtos.put("room", roomDtos);
        roomPlaceDtos.put("place", placeDto);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), roomPlaceDtos));

    }

    @ApiOperation(tags = "2. Host", value = "공간 상세 - 예약 조회", notes = "시설 예약 정보를 보여준다.")
    @GetMapping("/{placeId}/reservations")
    public ResponseEntity<SingleResponse<Map>> reservationPage(@RequestParam("memberId") Long id,
                                                               @PathVariable Long placeId) {

        hostService.hostCheck(id);

        PlaceResponse placeDto = hostService.findPlaceDtoByPlaceId(id, placeId);

        List<ReservationResponse> reservationDtos = hostService.findReservationDtoListByPlaceId(placeId);

        Map<String, Object> placeReservationDtos = new HashMap<>();
        placeReservationDtos.put("place", placeDto);
        placeReservationDtos.put("reservation", reservationDtos);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), placeReservationDtos));
    }

    @ApiOperation(tags = "2. Host", value = "예약 관리", notes = "호스트 입장에서 모든 시설의 예약현황을 보여준다.")
    @GetMapping("/reservations")
    public ResponseEntity<SingleResponse<Map>> allReservationsPage(@RequestParam("memberId") Long id) {

        hostService.hostCheck(id);

        Map<String, Object> placeReservationMap = hostService.createReservationDtoMapByEmail(id);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), placeReservationMap));
    }

    @ApiOperation(tags = "2. Host", value = "예약 상세", notes = "한 예약의 상세 페이지를 보여준다.")
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<SingleResponse<Map>> reservationDetailPage(@RequestParam("memberId") Long id,
                                                                     @PathVariable Long reservationId) {

        hostService.hostCheck(id);

        Map<String, Object> memberReservationPlaceDtos = hostService.getMemberReservationPlaceDtoMapByReservationId(reservationId, id);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), memberReservationPlaceDtos));
    }

    @ApiOperation(tags = "2. Host", value = "공간 등록", notes = "신규 공간을 등록한다.")
    @PostMapping("/place")
    public ResponseEntity placeRegister(@RequestParam("memberId") Long id,
                                              @Validated @RequestBody PlaceRoomReqeuest placeRoomReqeuest) {

        Member host = hostService.hostCheck(id);

        Long placeId = hostService.savePlaceAndRoomsByDto(placeRoomReqeuest, host);

        Map<String, Long> placeIdMap = new HashMap<>(){{put("placeId", placeId);}};


        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), placeIdMap));
    }

    @ApiOperation(tags = "2. Host", value = "공간 수정", notes = "기존 공간 정보를 수정한다.")
    @PutMapping("/place/{placeId}")
    public ResponseEntity placeUpdate(@RequestParam("memberId") Long id,
                                      @PathVariable("placeId") Long placeId,
                                      @Validated @RequestBody PlaceUpdateRequest placeUpdateRequest) {

        hostService.updatePlaceByDto(placeId, placeUpdateRequest, id);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), new Object()));


    }

    @ApiOperation(tags = "2. Host", value = "공간 삭제", notes = "기존 공간 정보를 삭제한다.")
    @DeleteMapping("/place/{placeId}")
    public ResponseEntity placeDelete(@RequestParam("memberId") Long id,
                                      @PathVariable("placeId") Long placeId) {

        hostService.deletePlace(placeId, id);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), new Object()));

    }

    @ApiOperation(tags = "2. Host", value = "방 수정", notes = "기존 방 정보를 수정한다.")
    @PutMapping("/room/{roomId}")
    public ResponseEntity roomUpdate(@RequestParam("memberId") Long id,
                                     @PathVariable("roomId") Long roomId,
                                     @RequestBody @Validated RoomReqeust roomReqeust) {

        hostService.updateRoomByDto(roomReqeust, roomId, id);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), new Object()));
    }

    @ApiOperation(tags = "2. Host", value = "방 삭제", notes = "기존 방 정보를 삭제한다.")
    @DeleteMapping("/room/{roomId}")
    public ResponseEntity roomDelete(@RequestParam("memberId") Long id,
                                     @PathVariable("roomId") Long roomId) {

        hostService.deleteRoomByRoomId(roomId, id);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), new Object()));
    }


    }
