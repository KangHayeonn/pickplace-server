package com.server.pickplace.member.service;

import com.server.pickplace.member.dto.MemberDetailResponse;
import com.server.pickplace.member.dto.MemberListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.server.pickplace.member.dto.MemberSaveRequest;
import com.server.pickplace.member.dto.MemberSaveResponse;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.error.MemberErrorResult;
import com.server.pickplace.member.error.MemberException;
import com.server.pickplace.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
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
public class MemberService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return memberRepository.findByEmail(username)
				.map(this::createUserDetails)
				.orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
	}

	// 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 리턴
	private UserDetails createUserDetails(Member member) {
		return User.builder()
				.username(member.getUsername())
				.password(passwordEncoder.encode(member.getPassword()))
				.roles(String.valueOf(member.getRole()))
				.build();
	}



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
