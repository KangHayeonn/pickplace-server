package com.server.pickplace.auth.controller;

import com.server.pickplace.auth.dto.MemberSignupRequestDto;
import com.server.pickplace.auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class SignupController {

    private final AuthService authService;

    @PostMapping(value = "signup")
    public String signup() {
        return "signup";
    }

    @PostMapping(value = "signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public String signup(@RequestBody MemberSignupRequestDto request) {
//        System.out.println("로그인 된 상태입니다.");
        return authService.signup(request);
    }
}
