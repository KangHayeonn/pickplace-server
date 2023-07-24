package com.server.pickplace.member.controller;

import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.host.dto.ReservationResponse;
import com.server.pickplace.member.dto.JwtRequestDto;
import com.server.pickplace.member.dto.mypageDto.MemberReservationResponseDto;
import com.server.pickplace.member.service.MemberInfoService;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")

public class MemberMyPageController {

    private final MemberInfoService memberInfoService;
    private final ResponseService responseService;

    @GetMapping("/reservation/members/{memberId}")
    public ResponseEntity memberReservation(@ApiIgnore HttpServletRequest httpServletRequest, @PathVariable Long memberId) throws Exception {
        List<MemberReservationResponseDto> reservationDtos = memberInfoService.reservationDetails(httpServletRequest, memberId);

        Map<String, Object> memberReservationDtos = new HashMap<>();


        memberReservationDtos.put("reservation", reservationDtos);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), memberReservationDtos)); // 성공
    }
}
