package com.server.pickplace.member.controller;

import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.member.dto.JwtRequestDto;
import com.server.pickplace.member.error.MemberErrorResult;
import com.server.pickplace.member.error.MemberException;
import com.server.pickplace.member.service.jwt.JwtTokenProvider;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping(value = "/api/v1/members")
public class MemberInfoController {

    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseService responseService;
    @ApiOperation(tags = "1. Member", value = "내 정보 조회", notes = "내 정보 조회한다")
    @GetMapping("/{memberId}")
    public ResponseEntity info(@ApiIgnore HttpServletRequest httpServletRequest, @RequestParam Long memberId) throws Exception {

//        String token = jwtTokenProvider.resolveToken((HttpServletRequest) httpServletRequest); //access Token 가져옴
        System.out.println(memberId);

//        if (token != null && jwtTokenProvider.validateToken(token)) {
//            System.out.println("godddd");
//        }


//        Map<String, Object> loginResponseDto = memberService.login(httpServletRequest,jwtRequestDto);
        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(),"회원가입 성공")); // 성공
    }
}
