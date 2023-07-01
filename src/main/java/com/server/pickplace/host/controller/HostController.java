package com.server.pickplace.host.controller;


import com.server.pickplace.common.dto.ListResponse;
import com.server.pickplace.common.dto.SingleResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.host.dto.*;
import com.server.pickplace.host.service.HostService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Base64.*;

@Tag(name = "2. Host", description = "Host API")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/host")
public class HostController {

    private final ResponseService responseService;
    private final HostService hostService;

    @ApiOperation(tags = "2. Host", value = "공간 관리 페이지", notes = "DB상에 존재하는 플레이스들을 보여준다.")
    @GetMapping("/place")
    public ResponseEntity<ListResponse<PlaceResponse>> placePage(@RequestHeader("Authorization") String accessToken) {

        Map<String, Object> payloadMap = getPayloadMap(accessToken); // 일단 토큰이 존재하고, 유효하다고 가정

        String email = (String) payloadMap.get("email");

        List<PlaceResponse> placeList = hostService.findPlaceDtoListByEmail(email); // null 가능

        return ResponseEntity.ok(responseService.getListResponse(HttpStatus.OK.value(), placeList));

    }  // null 가능

    @ApiOperation(tags = "2. Host", value = "공간 상세 - 방 조회", notes = "시설 내의 방 리스트를 보여준다.")
    @GetMapping("/{placeId}/rooms")
    public ResponseEntity<SingleResponse<Map>> roomPage(@RequestHeader("Authorization") String accessToken,
                                                                 @PathVariable Long placeId) {

        PlaceResponse placeDto = hostService.findPlaceDtoByPlaceId(placeId);

        List<RoomResponse> roomDtos = hostService.findRoomDtoListByPlaceId(placeId);

        Map<String, Object> roomPlaceDtos = new HashMap<>();

        roomPlaceDtos.put("room", roomDtos);
        roomPlaceDtos.put("place", placeDto);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), roomPlaceDtos));

    }

    @ApiOperation(tags = "2. Host", value = "공간 상세 - 예약 조회", notes = "시설 예약 정보를 보여준다.")
    @GetMapping("/{placeId}/reservations")
    public ResponseEntity<SingleResponse<Map>> reservationPage(@RequestHeader("Authorization") String accessToken, @PathVariable Long placeId) {

        PlaceResponse placeDto = hostService.findPlaceDtoByPlaceId(placeId);

        List<ReservationResponse> reservationDtos = hostService.findReservationDtoListByPlaceId(placeId);

        Map<String, Object> placeReservationDtos = new HashMap<>();

        placeReservationDtos.put("place", placeDto);
        placeReservationDtos.put("reservation", reservationDtos);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), placeReservationDtos));
    }

    @ApiOperation(tags = "2. Host", value = "예약 관리", notes = "호스트 입장에서 모든 시설의 예약현황을 보여준다.")
    @GetMapping("/reservations")
    public ResponseEntity<SingleResponse<Map>> allReservationsPage(@RequestHeader("Authorization") String accessToken) {

        Map<String, Object> payloadMap = getPayloadMap(accessToken);
        String email = (String) payloadMap.get("email");

        Map<String, List<ReservationResponse>> placeReservationMap = hostService.createReservationDtoMapByEmail(email);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), placeReservationMap));
    }

    @ApiOperation(tags = "2. Host", value = "예약 상세", notes = "한 예약의 상세 페이지를 보여준다.")
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<SingleResponse<Map>> reservationDetailPage(@RequestHeader("Authorization") String accessToken,
                                                                      @PathVariable Long reservationId) {

        Map<String, Object> memberReservationPlaceDtos = hostService.getMemberReservationPlaceDtoMapByReservationId(reservationId);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), memberReservationPlaceDtos));
    }

    @ApiOperation(tags = "2. Host", value = "공간 등록", notes = "신규 공간을 등록한다.") // 방만 추가하는 페이지도 필요할듯
    @PostMapping("/place")
    public ResponseEntity<Void> placeRegister(@RequestHeader("Authorization") String accessToken,
                                              @RequestBody PlaceRoomReqeuest placeRoomReqeuest) {

        PlaceRequest placeRequest = placeRoomReqeuest.getPlace();
        List<RoomReqeust> roomReqeusts = placeRoomReqeuest.getRooms();

        hostService.savePlaceByDto(placeRequest);

        hostService.saveRoomByDtos(roomReqeusts);

        return ResponseEntity.ok(null);
    }


    // 일단 BASE64 -> payload의 "email" 디코딩해서 리턴하는 것으로 설정... ( 추후 변경 가능 )
    private Map<String, Object> getPayloadMap(String accessToken) {

        String payloadJWT = accessToken.split("\\.")[1];
        Decoder decoder = getUrlDecoder();

        String payload = new String(decoder.decode(payloadJWT));
        JsonParser jsonParser = new BasicJsonParser();
        Map<String, Object> jsonArray = jsonParser.parseMap(payload);
        return jsonArray;
    }



}
