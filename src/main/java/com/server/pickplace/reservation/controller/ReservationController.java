package com.server.pickplace.reservation.controller;

import com.server.pickplace.common.dto.SingleResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.reservation.service.ReservationService;
import com.server.pickplace.search.service.SearchService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;

import static java.util.Base64.getUrlDecoder;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/reservation")
@Slf4j
public class ReservationController {

    private final ResponseService responseService;
    private final ReservationService reservationService;

    @ApiOperation(tags = "4. Reservation", value = "예약페이지 접근", notes = "상세페이지 공간안내/예약에서 예약 버튼을 눌렀을때 이동하는 페이지")
    @GetMapping("/{roomId}")
    public ResponseEntity<SingleResponse<Map>> reservationPage(@RequestHeader("Authorization") String accessToken,
                                                               @PathVariable("roomId") Long roomId) {

        Map<String, Object> payloadMap = getPayloadMap(accessToken); // 일단 토큰이 존재하고, 유효하다고 가정
        String email = (String) payloadMap.get("email");

        Map<String, Object> reservationPageMapByEmailAndRoomId = reservationService.getReservationPageMapByEmailAndRoomId(email, roomId);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), reservationPageMapByEmailAndRoomId));

    }

















    private Map<String, Object> getPayloadMap(String accessToken) {

        String payloadJWT = accessToken.split("\\.")[1];
        Base64.Decoder decoder = getUrlDecoder();

        String payload = new String(decoder.decode(payloadJWT));
        JsonParser jsonParser = new BasicJsonParser();
        Map<String, Object> jsonArray = jsonParser.parseMap(payload);
        return jsonArray;
    }




}
