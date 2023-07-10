//package com.server.pickplace.auth.controller;
//
//import com.server.pickplace.auth.dto.JwtRequestDto;
//import com.server.pickplace.auth.dto.JwtResponseDto;
//import com.server.pickplace.auth.dto.TokenInfo;
//import com.server.pickplace.auth.service.AuthService;
//import lombok.AllArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.*;
//
////@RestController
////@RequestMapping("/api/v1/members")
////@AllArgsConstructor
////public class LoginController {
////    private final AuthService authService;
////
////    @GetMapping(value = "login")
////    public String signup() {
////        return "login";
////    }
////    @PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_VALUE)
////    public TokenInfo login(@RequestBody JwtRequestDto jwtRequestDto) throws Exception {
////        String email = jwtRequestDto.getEmail();
////        String password = jwtRequestDto.getPassword();
////        TokenInfo tokenInfo = authService.login(email,password);
////        return tokenInfo;
////    }
////}
