package com.server.pickplace.member.service;

import com.server.pickplace.auth.dto.JwtRequestDto;
import com.server.pickplace.member.dto.MemberSignupRequestDto;
import com.server.pickplace.auth.dto.TokenInfo;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.config.Helper;
import com.server.pickplace.member.dto.EmailCheckRequestDto;
import com.server.pickplace.member.dto.LoginResponseDto;
import com.server.pickplace.member.dto.MemberDetailResponse;
import com.server.pickplace.member.dto.MemberListResponse;
import com.server.pickplace.member.entity.MemberRole;
import com.server.pickplace.member.repository.RefreshTokenRedisRepository;
import com.server.pickplace.member.service.jwt.JwtTokenProvider;
import com.server.pickplace.member.service.jwt.RefreshToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.stream.Collectors;

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

	@Transactional
	//login service
	public Map<String, Object> login(HttpServletRequest request, JwtRequestDto jwtRequestDto) throws Exception {


		long a = loginFindByEmail(jwtRequestDto.getEmail()).getId();
		System.out.println(memberRepository.findByEmail(jwtRequestDto.getEmail()).get().getPassword());
		System.out.println(jwtRequestDto.getPassword());
//		UsernamePasswordAuthenticationToken authenticationToken = jwtRequestDto.toAuthentication();

		if (memberRepository.findByEmail(jwtRequestDto.getEmail()).orElse(null)==null)
			throw new MemberException(MemberErrorResult.UNKNOWN_EXCEPTION); //없는 아이디

		if (passwordEncoder.encode(memberRepository.findByEmail(jwtRequestDto.getEmail()).get().getPassword()).equals(jwtRequestDto.getPassword())) {
				throw new MemberException(MemberErrorResult.MEMBER_NOT_FOUND);
		}


		//email 과 password를 기반으로하는 Authentication 객체 생성
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jwtRequestDto.getEmail(),jwtRequestDto.getPassword());
//		// 실제 검증 -> loadUserByUsername 메서드 실행
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

	public ResponseEntity reissue(HttpServletRequest request) {

		//1. Request Header 에서 JWT Token 추출
		String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

		//2. validateToken 메서드로 토큰 유효성 검사
		if (token != null && jwtTokenProvider.validateToken(token)) {
			//3. refresh token 인지 확인
			if (jwtTokenProvider.isRefreshToken(token)) {
				//refresh token
				RefreshToken refreshToken = refreshTokenRedisRepository.findByRefreshToken(token);
				if (refreshToken != null) {
					//4. 최초 로그인한 ip 와 같은지 확인 (처리 방식에 따라 재발급을 하지 않거나 메일 등의 알림을 주는 방법이 있음)
					String currentIpAddress = Helper.getClientIp(request);
					if (refreshToken.getIp().equals(currentIpAddress)) {
						// 5. Redis 에 저장된 RefreshToken 정보를 기반으로 JWT Token 생성
						TokenInfo tokenInfo = jwtTokenProvider.generateToken(refreshToken.getId(), refreshToken.getAuthorities());

						// 4. Redis RefreshToken update
						refreshTokenRedisRepository.save(RefreshToken.builder()
								.id(refreshToken.getId())
								.ip(currentIpAddress)
								.authorities(refreshToken.getAuthorities())
								.refreshToken(tokenInfo.getRefreshToken())
								.build());

						return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(),"갱신 성공"));
					}
				}
			}
		}

		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(),"갱신 실패"));
	}




	@Transactional
	public String signup(MemberSignupRequestDto request) {

		//db에 저장
		Member member = Member.builder()
				.email(request.getEmail())
				.password(request.getPassword())
				.number(request.getPhone())
				.name(request.getNickname())
				.role(MemberRole.valueOf(request.getMemberRole()))
				.build();



		return memberRepository.save(member).getEmail(); //예외 처리 메시지 추가 예정----------->
	}

	public boolean emailCheck(EmailCheckRequestDto email) {
//		memberRepository.findByEmail(email).get().getEmail()
		System.out.println(email.getEmail());

		boolean existMember = memberRepository.existsByEmail(email.getEmail());
//		System.out.println(memberRepository.existsByEmail(email));
		if (existMember) return false;
		else return true;
	}


	//email로 db 불러오기 (로그인)
	public Member loginFindByEmail(final String userEmail) {
		Optional<Member> optionalPlace = memberRepository.findByEmail(userEmail);

		Member email = optionalPlace.orElseThrow(() -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));

		return email;
	}



//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		return memberRepository.findByEmail(username)
//				.map(this::createUserDetails)
//				.orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
//	}
//
//	// 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 리턴
//	private UserDetails createUserDetails(Member member) {
//		return User.builder()
//				.username(member.getUsername())
//				.password(passwordEncoder.encode(member.getPassword()))
//				.roles(String.valueOf(member.getRole()))
//				.build();
//	}



//	public MemberSaveResponse addMember(MemberSaveRequest memberSaveRequest) {
//		final Member findMember = memberRepository.findByEmail(memberSaveRequest.getEmail());
//
//		if (findMember != null) {
//			log.error(findMember.getName());
//			throw new MemberException(MemberErrorResult.DUPLICATED_MEMBER_REGISTER);
//		}
//
//		Member member = memberRepository.save(Member.builder()
//			.email(memberSaveRequest.getEmail())
//			.name(memberSaveRequest.getName())
//			.build());
//
//		return MemberSaveResponse.builder()
//			.id(member.getId())
//			.email(member.getEmail())
//			.name(member.getName())
//			.build();
//	}

	public List<MemberListResponse> getMemberListByName(String name) {
		List<Member> memberList = memberRepository.findByName(name);

		return memberList.stream()
				.map(member -> MemberListResponse.builder()
						.id(member.getId())
						.email(member.getEmail())
						.name(member.getName())
						.build())
				.collect(Collectors.toList());
	}

	public MemberDetailResponse getMember(Long id) {
		final Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
		return MemberDetailResponse.builder()
				.id(member.getId())
				.name(member.getName())
				.email(member.getEmail())
				.build();
	}

    public void deleteMember(Long id) {
		memberRepository.findById(id).orElseThrow(() -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
		memberRepository.deleteById(id);
    }
}
