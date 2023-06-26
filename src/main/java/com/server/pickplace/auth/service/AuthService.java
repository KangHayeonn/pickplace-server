package com.server.pickplace.auth.service;

import com.server.pickplace.auth.dto.JwtRequestDto;
import com.server.pickplace.auth.dto.MemberSignupRequestDto;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.repository.MemberRepository;
import com.server.pickplace.member.service.UserDetail;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    //login service
    public String login(JwtRequestDto request) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetail principal = (UserDetail) authentication.getPrincipal();
        return principal.getUsername();
    }

    //회원 가입 Service
    public String signup(MemberSignupRequestDto request) {
        boolean existMember = memberRepository.existsByEmail(request.getEmail());

        // 이미 회원이 존재하는 경우
        if (existMember) return null;

        Member member = new Member(request);
        member.encryptPassword(passwordEncoder);

        memberRepository.save(member);
        return member.getEmail();
    }
}
