package com.server.pickplace.host.controller;


import com.server.pickplace.common.dto.ListResponse;
import com.server.pickplace.common.dto.SingleResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.host.dto.*;
import com.server.pickplace.host.error.HostErrorResult;
import com.server.pickplace.host.error.HostException;
import com.server.pickplace.host.repository.HostRepository;
import com.server.pickplace.host.service.HostService;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.entity.MemberRole;
import com.server.pickplace.place.entity.CategoryStatus;
import com.server.pickplace.place.entity.TagStatus;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Base64.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/host")
@Slf4j
public class HostController {

    private final ResponseService responseService;
    private final HostService hostService;

    @ApiOperation(tags = "2. Host", value = "공간 관리 페이지", notes = "DB상에 존재하는 플레이스들을 보여준다.")
    @GetMapping("/place")
    public ResponseEntity<ListResponse<PlaceResponse>> placePage(@RequestHeader("accessToken") String accessToken) {

        String email = hostService.getPayloadMapAndGetEmail(accessToken);

        hostService.hostCheck(email);
        
        List<PlaceResponse> placeList = hostService.findPlaceDtoListByEmail(email);

        return ResponseEntity.ok(responseService.getListResponse(HttpStatus.OK.value(), placeList));

    }

    @ApiOperation(tags = "2. Host", value = "공간 상세 - 방 조회", notes = "시설 내의 방 리스트를 보여준다.")
    @GetMapping("/{placeId}/rooms")
    public ResponseEntity<SingleResponse<Map>> roomPage(@RequestHeader("accessToken") String accessToken,
                                                        @PathVariable Long placeId) {

        String email = hostService.getPayloadMapAndGetEmail(accessToken);
        hostService.hostCheck(email);

        PlaceResponse placeDto = hostService.findPlaceDtoByPlaceId(email, placeId);

        List<RoomResponse> roomDtos = hostService.findRoomDtoListByPlaceId(placeId);

        Map<String, Object> roomPlaceDtos = new HashMap<>();
        roomPlaceDtos.put("room", roomDtos);
        roomPlaceDtos.put("place", placeDto);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), roomPlaceDtos));

    }

    @ApiOperation(tags = "2. Host", value = "공간 상세 - 예약 조회", notes = "시설 예약 정보를 보여준다.")
    @GetMapping("/{placeId}/reservations")
    public ResponseEntity<SingleResponse<Map>> reservationPage(@RequestHeader("accessToken") String accessToken,
                                                               @PathVariable Long placeId) {

        String email = hostService.getPayloadMapAndGetEmail(accessToken);
        hostService.hostCheck(email);


        PlaceResponse placeDto = hostService.findPlaceDtoByPlaceId(email, placeId);

        List<ReservationResponse> reservationDtos = hostService.findReservationDtoListByPlaceId(placeId);

        Map<String, Object> placeReservationDtos = new HashMap<>();
        placeReservationDtos.put("place", placeDto);
        placeReservationDtos.put("reservation", reservationDtos);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), placeReservationDtos));
    }

    @ApiOperation(tags = "2. Host", value = "예약 관리", notes = "호스트 입장에서 모든 시설의 예약현황을 보여준다.")
    @GetMapping("/reservations")
    public ResponseEntity<SingleResponse<Map>> allReservationsPage(@RequestHeader("accessToken") String accessToken) {

        String email = hostService.getPayloadMapAndGetEmail(accessToken);
        hostService.hostCheck(email);

        Map<String, Object> placeReservationMap = hostService.createReservationDtoMapByEmail(email);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), placeReservationMap));
    }

    @ApiOperation(tags = "2. Host", value = "예약 상세", notes = "한 예약의 상세 페이지를 보여준다.")
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<SingleResponse<Map>> reservationDetailPage(@RequestHeader("accessToken") String accessToken,
                                                                     @PathVariable Long reservationId) {

        String email = hostService.getPayloadMapAndGetEmail(accessToken);
        hostService.hostCheck(email);

        Map<String, Object> memberReservationPlaceDtos = hostService.getMemberReservationPlaceDtoMapByReservationId(reservationId, email);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), memberReservationPlaceDtos));
    }

    @ApiOperation(tags = "2. Host", value = "공간 등록", notes = "신규 공간을 등록한다.") // 방만 추가하는 페이지도 필요할듯
    @PostMapping("/place")
    public ResponseEntity placeRegister(@RequestHeader("accessToken") String accessToken,
                                              @Validated @RequestBody PlaceRoomReqeuest placeRoomReqeuest) {

        String email = hostService.getPayloadMapAndGetEmail(accessToken);
        Member host = hostService.hostCheck(email);

        hostService.savePlaceAndRoomsByDto(placeRoomReqeuest, host);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), null));
    }



}
