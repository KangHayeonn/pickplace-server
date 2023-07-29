package com.server.pickplace.mypage;

import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.mypage.dto.MyPageReservationMoreResponseDto;
import com.server.pickplace.mypage.dto.MyPageReservationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
public class MyPageController {
    private final MyPageService myPageService;
    private final ResponseService responseService;

    @GetMapping("/reservation/members/{memberId}")
    public ResponseEntity memberReservation(@ApiIgnore HttpServletRequest httpServletRequest, @PathVariable Long memberId) throws Exception {

        List<MyPageReservationResponseDto> reservationDtos = myPageService.reservationDetails(httpServletRequest, memberId);

        Map<String, Object> memberReservationDtos = new HashMap<>();
        memberReservationDtos.put("reservation", reservationDtos);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), memberReservationDtos)); // 성공
    }

    @GetMapping("/reservation/detail/{reservationId}")
    public ResponseEntity memberReservationDetails(@ApiIgnore HttpServletRequest httpServletRequest, @PathVariable Long reservationId) throws Exception {

        List<MyPageReservationMoreResponseDto> reservationDtos = myPageService.reservationDetailsMore(httpServletRequest, reservationId);

        Map<String, Object> memberReservationDtos = new HashMap<>();
        memberReservationDtos.put("reservation", reservationDtos);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), memberReservationDtos)); // 성공
    }
}
