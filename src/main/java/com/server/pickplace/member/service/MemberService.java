package com.server.pickplace.member.service;

import com.server.pickplace.auth.dto.JwtRequestDto;
import com.server.pickplace.auth.dto.TokenInfo;
import com.server.pickplace.member.dto.LoginResponseDto;
import com.server.pickplace.member.dto.MemberDetailResponse;
import com.server.pickplace.member.dto.MemberListResponse;
import com.server.pickplace.member.service.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.error.MemberErrorResult;
import com.server.pickplace.member.error.MemberException;
import com.server.pickplace.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import java.util.List;
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

	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtTokenProvider jwtTokenProvider;

	@Transactional
	//login service
	public LoginResponseDto login(JwtRequestDto jwtRequestDto) throws Exception {


//		UsernamePasswordAuthenticationToken authenticationToken = jwtRequestDto.toAuthentication();

		//email 과 password를 기반으로하는 Authentication 객체 생성
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jwtRequestDto.getEmail(),jwtRequestDto.getPassword());
		// 실제 검증 -> loadUserByUsername 메서드 실행
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		// 3. 인증 정보를 기반으로 JWT 토큰 생성
		TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);


		//로그인 성공 api 전송
		return LoginResponseDto.builder()
				.memberId(memberRepository.findByEmail(jwtRequestDto.getEmail()).get().getId())
				.nickname(memberRepository.findByEmail(jwtRequestDto.getEmail()).get().getName())
				.accessToken(tokenInfo.getAccessToken())
				.refreshToken(tokenInfo.getRefreshToken())
				.build();
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
