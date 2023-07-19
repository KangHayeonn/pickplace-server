package com.server.pickplace.socialLogin.kakao.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.socialLogin.kakao.dto.SocialUserInfoDto;
//import com.server.pickplace.socialLogin.kakao.service.KakaoUserService;
import com.server.pickplace.socialLogin.kakao.service.KakaoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public ResponseEntity kakaoLogin(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        Map<String, Object> loginResponseDto = kakaoUserService.kakaoLogin(code, request ,response);
        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), loginResponseDto)); // 성공
    }
}
