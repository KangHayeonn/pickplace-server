package com.server.pickplace.member.service;

import com.server.pickplace.member.dto.*;
import com.server.pickplace.member.dto.TokenInfo;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.config.Helper;
import com.server.pickplace.member.entity.MemberRole;
import com.server.pickplace.member.repository.RefreshTokenRedisRepository;
import com.server.pickplace.member.service.jwt.JwtTokenProvider;
import com.server.pickplace.member.service.jwt.RefreshToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.error.MemberErrorResult;
import com.server.pickplace.member.error.MemberException;
import com.server.pickplace.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;

import static java.util.Base64.getUrlDecoder;

/**
 * description    :
 * packageName    : com.server.pickplace.member.service
 * fileName       : MemberService
 * author         : tkfdk
 * date           : 2023-05-28
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-05-28        tkfdk       최초 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
//@AllArgsConstructor
public class MemberService {


	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	private final RefreshTokenRedisRepository refreshTokenRedisRepository;
	private final ResponseService responseService;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	StringRedisTemplate redisTemplate;

	@Transactional
	//login service
	public Map<String, Object> login(HttpServletRequest request, JwtRequestDto jwtRequestDto) throws Exception {

		if (memberRepository.findByEmail(jwtRequestDto.getEmail()).orElse(null) == null)
			throw new MemberException(MemberErrorResult.MEMBER_NOT_ID); //없는 아이디

		if (!memberRepository.findByEmail(jwtRequestDto.getEmail()).get().getPassword().equals(jwtRequestDto.getPassword())) {
			throw new MemberException(MemberErrorResult.MEMBER_NOT_PW); // 비밀번호 틀린 경우
		}

		//email 과 password를 기반으로하는 Authentication 객체 생성
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jwtRequestDto.getEmail(), jwtRequestDto.getPassword());
		// 실제 검증 -> loadUserByUsername 메서드 실행
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		// 3. 인증 정보를 기반으로 JWT 토큰 생성
		TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

		// 4. Redis RefreshToken 저장
		refreshTokenRedisRepository.save(RefreshToken.builder()
				.id(authentication.getName())
				.ip(Helper.getClientIp(request))
				.authorities(authentication.getAuthorities())
				.refreshToken(tokenInfo.getRefreshToken())
				.build());

		Map<String, Object> loginMap = new HashMap<>();

		LoginResponseDto loginResponseDtoDto = LoginResponseDto.builder()
				.memberId(memberRepository.findByEmail(jwtRequestDto.getEmail()).get().getId())
				.nickname(memberRepository.findByEmail(jwtRequestDto.getEmail()).get().getName())
				.accessToken(tokenInfo.getAccessToken())
				.refreshToken(tokenInfo.getRefreshToken())
				.build();

		loginMap.put("member", loginResponseDtoDto);

		//로그인 성공 api 전송
		return loginMap;
	}

	public Map<String, Object> reissue(String id, ReissueRequestDto requestDto) {

		Optional<RefreshToken> refreshToken
				= refreshTokenRedisRepository.findByRefreshToken(requestDto.getRefreshToken());
//
		RefreshToken refreshToken1 = refreshToken.orElseThrow(() -> new MemberException(MemberErrorResult.UNKNOWN_TOKEN)); // 유효하지 않은 refresh 토큰 예외처리


		if (!id.equals(refreshToken.get().getId())) {
			throw new MemberException(MemberErrorResult.UNKNOWN_TOKEN); // // 유효하지 않은 refresh 토큰 예외처리
		}

		String role = refreshToken.get().getAuthorities().toString();
		role.replace("[", "");
		role.replace("]", "");

		//유효한 거 확인 된 경우만 넘어옴 // 재발급 과정
		String accessToken = jwtTokenProvider.recreationAccessToken(id, role, requestDto.getRefreshToken());

		Map<String, Object> reissueMap = new HashMap<>();

		ReissueResponseDto reissueResponseDto = ReissueResponseDto.builder()
				.accessToken(accessToken)
				.refreshToken(requestDto.getRefreshToken())
				.build();

		reissueMap.put("member", reissueResponseDto);

		return reissueMap;
	}

	@Transactional
	public String signup(MemberSignupRequestDto request) {

		//db에 저장
		Member member = Member.builder()
				.email(request.getEmail())
				.password(request.getPassword())
				.number(request.getPhone())
				.name(request.getNickname())
				.type("common")
				.role(MemberRole.valueOf(request.getMemberRole()))
				.build();

		return memberRepository.save(member).getEmail();
	}

	public boolean emailCheck(EmailCheckRequestDto email) {

		boolean existMember = memberRepository.existsByEmail(email.getEmail());
		if (existMember) return false;
		else return true;
	}


	public void logout(HttpServletRequest request) {
		String token = jwtTokenProvider.resolveToken((HttpServletRequest) request); //access Token 가져옴
		ValueOperations<String, String> logoutValueOperations = redisTemplate.opsForValue(); //access Token 블랙리스트에 등록
		logoutValueOperations.set(token, token);


		String payloadJWT = token.split("\\.")[1];
		Base64.Decoder decoder = getUrlDecoder();

		String payload = new String(decoder.decode(payloadJWT));
		JsonParser jsonParser = new BasicJsonParser();
		Map<String, Object> jsonArray = jsonParser.parseMap(payload);
		String email = (String) jsonArray.get("sub"); // id 담아옴

		// refreshToken 삭제
		refreshTokenRedisRepository.findById(email)
				.orElseThrow(() -> new MemberException(MemberErrorResult.ALREADY_LOGOUT)); //로그인 안된 사용자 예외처리

		refreshTokenRedisRepository.deleteById(email); // 토큰 삭제


	}

	public void deleteMember(Long memberId) {


		memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND)); //존재하지 않는 회원 에외처리

		memberRepository.deleteById(memberId);

	}


}
