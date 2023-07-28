package com.server.pickplace.socialLogin.kakao.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.member.dto.login.KakaoCodeDto;
import com.server.pickplace.socialLogin.kakao.dto.SocialUserInfoDto;
//import com.server.pickplace.socialLogin.kakao.service.KakaoUserService;
import com.server.pickplace.socialLogin.kakao.service.KakaoUserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.service.OAuth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/members")
public class SocialLoginController {
    private final KakaoUserService kakaoUserService;
    private final ResponseService responseService;

    @ApiOperation(tags = "1. Member", value = "소셜로그인(카카오)", notes = "소셜 로그인 코드를 받아 처리한다")
    @PostMapping("/kakaoLogin")
    public ResponseEntity kakaoLogin(HttpServletRequest request, HttpServletResponse response, @RequestBody KakaoCodeDto code) throws JsonProcessingException {

        Map<String, Object> loginResponseDto = kakaoUserService.kakaoLogin(code.getCode(), request ,response);
        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), loginResponseDto)); // 성공
    }


}
