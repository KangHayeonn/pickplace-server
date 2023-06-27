package com.server.pickplace.auth.service;

import com.server.pickplace.auth.dto.JwtRequestDto;
import com.server.pickplace.auth.dto.JwtResponseDto;
import com.server.pickplace.auth.dto.MemberSignupRequestDto;
import com.server.pickplace.security.jwt.JwtTokenProvider;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.repository.MemberRepository;
import com.server.pickplace.security.UserDetail;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final JwtTokenProvider jwtTokenProvider;

    //login service
    public JwtResponseDto login(JwtRequestDto request) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        UserDetail principal = (UserDetail) authentication.getPrincipal();
//        return principal.getUsername();

        return createJwtToken(authentication);
    }

    private JwtResponseDto createJwtToken(Authentication authentication) {
        UserDetail principal = (UserDetail) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(principal);
        return new JwtResponseDto(token);
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
