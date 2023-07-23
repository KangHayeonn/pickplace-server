package com.server.pickplace.member.controller;

import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.member.dto.JwtRequestDto;
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
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")

public class MemberMyPageController {

    private final MemberInfoService memberInfoService;
    private final ResponseService responseService;

    @GetMapping("/reservation/members/{memberId}")
    public ResponseEntity memberReservation(@ApiIgnore HttpServletRequest httpServletRequest, @PathVariable Long memberId) throws Exception {
        String infoResponseDto = memberInfoService.reservationDetails(httpServletRequest, memberId);
        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), infoResponseDto)); // 성공
    }
}
