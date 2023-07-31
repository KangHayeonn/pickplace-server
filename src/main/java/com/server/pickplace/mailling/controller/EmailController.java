package com.server.pickplace.mailling.controller;


import com.server.pickplace.mailling.dto.EmailMessage;
import com.server.pickplace.mailling.dto.EmailPostDto;
import com.server.pickplace.mailling.service.EmailService;
import com.server.pickplace.member.dto.MemberIdRequestDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@RequestMapping("/api/v1/members")
@RestController
@RequiredArgsConstructor

public class EmailController {

    private final JavaMailSender javaMailSender;
    private final EmailService emailService;


    @ApiOperation(tags = "1. Member", value = "비밀번호 찾기/변경", notes = "비밀번호를 찾기 위해 인증번호를 발급 받는다")
    @PostMapping("/pwd")
    public ResponseEntity sendPasswordMail(@RequestBody EmailPostDto emailPostDto) throws MessagingException {

        String email = emailPostDto.getEmail();

        EmailMessage emailMessage = EmailMessage.builder()
                .to(emailPostDto.getEmail())
                .subject("[PICKPLACE] 임시 비밀번호 발급")
                .build();

        emailService.sendMail(emailMessage,"password");
//        이메일 전송 샘플
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setTo("sophia4130@naver.com");
//        simpleMailMessage.setSubject("sub");
//        simpleMailMessage.setText("test");
//        javaMailSender.send(simpleMailMessage);

        return ResponseEntity.ok().build();
    }

    @ApiOperation(tags = "1. Member", value = "비밀번호 찾기/변경", notes = "비밀번호를 찾기 위해 인증번호를 발급 받는다")
    @PutMapping("/pwd")
    public ResponseEntity editPasswordMail(@RequestBody EmailPostDto emailPostDto) throws MessagingException{

        emailPostDto.
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