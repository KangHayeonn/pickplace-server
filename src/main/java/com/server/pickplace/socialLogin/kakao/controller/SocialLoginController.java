package com.server.pickplace.socialLogin.kakao.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.server.pickplace.socialLogin.kakao.dto.SocialUserInfoDto;
//import com.server.pickplace.socialLogin.kakao.service.KakaoUserService;
import com.server.pickplace.socialLogin.kakao.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.service.OAuth;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/members")
public class SocialLoginController {
//    private final KakaoUserService kakaoUserService;
    private final OAuthService oAuthService;

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        return oAuthService.getKakaoAccessToken(code);
//        return kakaoUserService.kakaoLogin(code, response);
    }
}
