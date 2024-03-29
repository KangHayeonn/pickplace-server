package com.server.pickplace.mailling.controller;


import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.mailling.dto.EmailMessage;
import com.server.pickplace.mailling.dto.EmailPostDto;
import com.server.pickplace.mailling.dto.EmailResponseDto;
import com.server.pickplace.mailling.dto.PassWordEditDto;
import com.server.pickplace.mailling.service.EmailService;
import com.server.pickplace.member.dto.MemberIdRequestDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import springfox.documentation.annotations.ApiIgnore;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;


@RequestMapping("/api/v1/members")
@RestController
@RequiredArgsConstructor

public class EmailController {

    private final JavaMailSender javaMailSender;
    private final EmailService emailService;
    private final ResponseService responseService;


    @ApiOperation(tags = "1. Member", value = "비밀번호 찾기/변경 메일 발송", notes = "비밀번호를 찾기 위해 인증번호를 발급 받는다")
    @PostMapping("/pwd")
    public ResponseEntity sendPasswordMail(@RequestBody EmailPostDto emailPostDto) throws MessagingException {

        String email = emailPostDto.getEmail();

        EmailMessage emailMessage = EmailMessage.builder()
                .to(emailPostDto.getEmail())
                .subject("[PICK PLACE] 임시 비밀번호 발급 ")
                .build();

        String code = emailService.sendMail(emailMessage,"mail");

        EmailResponseDto emailResponseDto = new EmailResponseDto();
        emailResponseDto.setCode(code);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), emailResponseDto)); //
    }

    @ApiOperation(tags = "1. Member", value = "비밀번호 변경", notes = "비밀번호 변경한다.")
    @PutMapping("/pwd")
    public ResponseEntity editPasswordMail(@ApiIgnore HttpServletRequest httpServletRequest , @RequestBody PassWordEditDto passWordEditDto) throws MessagingException{

        String code = emailService.updatePassword(httpServletRequest,passWordEditDto);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), "비밀번호 변경 완료")); //
    }

    @ApiOperation(tags = "1. Member", value = "이메일 인증", notes = "이메일 인증한다")
    @PostMapping("/email")
    public ResponseEntity sendJoinMail(@RequestBody EmailPostDto emailPostDto) {

        EmailMessage emailMessage = EmailMessage.builder()
                .to(emailPostDto.getEmail())
                .subject("[PICK PLACE] 이메일 인증을 위한 인증 코드 발송")
                .build();

        String code = emailService.sendMail(emailMessage, "mail");

        EmailResponseDto emailResponseDto = new EmailResponseDto();
        emailResponseDto.setCode(code);

        return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), emailResponseDto)); //
    }


}