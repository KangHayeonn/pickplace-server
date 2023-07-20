package com.server.pickplace.mailling.controller;

import com.server.pickplace.mailling.dto.EmailMessage;
import com.server.pickplace.mailling.service.EmailService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/send-mail")
@RestController
@RequiredArgsConstructor

public class EmailController {
    private final EmailService emailService;

    private final JavaMailSender javaMailSender;


    @ApiOperation(tags = "1. Member", value = "메일", notes = "메일 관련")
    @PostMapping("/pwd")
    public ResponseEntity sendPasswordMail() {
//        EmailMessage emailMessage = EmailMessage.builder()
//                .to(emailPostDto.getEmail()) //수신자
//                .subject("[SAVIEW] 임시 비밀번호 발급") // 제목
//                .build();
//
//        emailService.sendMail(emailMessage, "password");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo("sophia4130@naver.com");
        simpleMailMessage.setSubject("sub");
        simpleMailMessage.setText("test");
        javaMailSender.send(simpleMailMessage);

        return ResponseEntity.ok().build();
    }

    // 회원가입 이메일 인증 - 요청 시 body로 인증번호 반환하도록 작성하였음
//    @PostMapping("/email")
//    public ResponseEntity sendJoinMail(@RequestBody EmailPostDto emailPostDto) {
//        EmailMessage emailMessage = EmailMessage.builder()
//                .to(emailPostDto.getEmail())
//                .subject("[SAVIEW] 이메일 인증을 위한 인증 코드 발송")
//                .build();
//
//        String code = emailService.sendMail(emailMessage, "email");
//
//        EmailResponseDto emailResponseDto = new EmailResponseDto();
//        emailResponseDto.setCode(code);
//
//        return ResponseEntity.ok(emailResponseDto);
//    }
}
