package com.server.pickplace.mailling.service;

import com.server.pickplace.mailling.dto.EmailMessage;
import com.server.pickplace.mailling.dto.PassWordEditDto;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.error.MemberErrorResult;
import com.server.pickplace.member.error.MemberException;
import com.server.pickplace.member.repository.MemberRepository;
import com.server.pickplace.member.service.MemberInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final MemberRepository memberRepository;
    private final MemberInfoService memberInfoService;
    private final SpringTemplateEngine templateEngine;
    @Autowired
    private PasswordEncoder pwEncoder;

    public String sendMail(EmailMessage emailMessage, String type) {
        String authNum = createCode();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();


        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo()); // 메일 수신자
            mimeMessageHelper.setSubject(emailMessage.getSubject()); // 메일 제목
            mimeMessageHelper.setText(setContext(authNum, type), true); //코드
            javaMailSender.send(mimeMessage);
            return authNum;
        } catch (MessagingException e) {

            throw new RuntimeException(e);

        }
    }

    // 인증번호 및 임시 비밀번호 생성
    public String createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0: key.append((char) ((int) random.nextInt(26) + 97)); break;
                case 1: key.append((char) ((int) random.nextInt(26) + 65)); break;
                default: key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }

    @Transactional
    public String updatePassword(HttpServletRequest httpServletRequest, PassWordEditDto passWordEditDto){
        String email = passWordEditDto.getEmail();
        String pw = passWordEditDto.getPassword();


        Member member = memberRepository.findByEmailAndType(email,"common").orElseThrow(()-> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
        memberInfoService.checkInfoValid(httpServletRequest,member.getId());
        String encodePw = pwEncoder.encode(pw);
        member.setPassword(encodePw);

        return pw;
    }

    public String setContext(String code, String type) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(type, context);
    }
}

