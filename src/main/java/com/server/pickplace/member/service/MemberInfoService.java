package com.server.pickplace.member.service;

import com.server.pickplace.member.dto.InfoNicknameRequestDto;
import com.server.pickplace.member.dto.InfoPhoneRequestDto;
import com.server.pickplace.member.dto.InfoResponseDto;
import com.server.pickplace.member.dto.LoginResponseDto;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.error.MemberErrorResult;
import com.server.pickplace.member.error.MemberException;
import com.server.pickplace.member.repository.MemberRepository;
import com.server.pickplace.member.service.jwt.JwtTokenProvider;
import io.jsonwebtoken.io.Decoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.util.Base64.getUrlDecoder;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberInfoService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public Map<String, Object> info(HttpServletRequest httpServletRequest, Long memberId){

        String token = jwtTokenProvider.resolveToken((HttpServletRequest) httpServletRequest); //access Token 가져옴

        Map<String, Object> InfoMap = new HashMap<>();

        //유효한 토큰만 통과 // 아닌건 예외처리
        if (token != null && jwtTokenProvider.validateToken(token)) {

            //존재하지 않는 회원 예외 처리
            memberRepository.findById(memberId).orElseThrow(()-> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

            String payloadJWT = token.split("\\.")[1];
            Base64.Decoder decoder = getUrlDecoder();

            String payload = new String(decoder.decode(payloadJWT));
            JsonParser jsonParser = new BasicJsonParser();
            Map<String, Object> jsonArray = jsonParser.parseMap(payload);
            String email = (String) jsonArray.get("sub");

            // 토큰의 주인과 id가 일치하는지 확인후 예외처리
            if(!memberRepository.findById(memberId).get().getEmail().equals(email)){
                throw new MemberException(MemberErrorResult.NOT_AUTHENTICATION);
            }

            InfoResponseDto infoResponseDto = InfoResponseDto.builder()
                    .email(memberRepository.findById(memberId).get().getEmail())
                    .phone(memberRepository.findById(memberId).get().getNumber())
                    .nickname(memberRepository.findById(memberId).get().getName())
                    .build();

            InfoMap.put("member", infoResponseDto);

            //로그인 성공 api 전송
            return InfoMap;

        }
        return InfoMap;
    }

    public void phoneUpdate(HttpServletRequest httpServletRequest, InfoPhoneRequestDto requestDto){

        Long id = requestDto.getMemberId();
        Member member = memberRepository.findById(requestDto.getMemberId()).orElseThrow(()-> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
        checkInfoValid(httpServletRequest,id);
        //비밀번호만 변경
        member.setNumber(requestDto.getPhone());

    }

    public void nicknameUpdate(HttpServletRequest httpServletRequest, InfoNicknameRequestDto requestDto){

        Long id = requestDto.getMemberId();
        Member member = memberRepository.findById(requestDto.getMemberId()).orElseThrow(()-> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

        checkInfoValid(httpServletRequest,id);
        //비밀번호만 변경
        member.setName(requestDto.getNickname());

    }

    public void checkInfoValid (HttpServletRequest httpServletRequest, Long id){
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) httpServletRequest);

        //유효한 토큰만 통과 // 아닌건 예외처리
        if (token != null && jwtTokenProvider.validateToken(token)) {
            //존재하지 않는 회원
//            Member member = memberRepository.findById(requestDto.getMemberId()).orElseThrow(()-> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
            String payloadJWT = token.split("\\.")[1];
            Base64.Decoder decoder = getUrlDecoder();

            String payload = new String(decoder.decode(payloadJWT));
            JsonParser jsonParser = new BasicJsonParser();
            Map<String, Object> jsonArray = jsonParser.parseMap(payload);
            String email = (String) jsonArray.get("sub");

            // 토큰의 주인과 id가 일치하는지 확인후 예외처리
            if(!memberRepository.findById(id).get().getEmail().equals(email)){
                throw new MemberException(MemberErrorResult.NOT_AUTHENTICATION);
            }
    }

}
}


