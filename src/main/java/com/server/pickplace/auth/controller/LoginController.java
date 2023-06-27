package com.server.pickplace.auth.controller;

import com.server.pickplace.auth.dto.JwtRequestDto;
import com.server.pickplace.auth.dto.JwtResponseDto;
import com.server.pickplace.auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class LoginController {
    private final AuthService authService;

    @GetMapping(value = "login")
    public String signup() {
        return "login";
    }
    @PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_VALUE)
    public JwtResponseDto login(@RequestBody JwtRequestDto request) {

        try {
            return authService.login(request);
        } catch (Exception e) {
            return new JwtResponseDto(e.getMessage());
        }
    }
}
