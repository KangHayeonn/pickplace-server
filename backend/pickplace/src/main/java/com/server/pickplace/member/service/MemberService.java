package com.server.pickplace.member.service;

import com.server.pickplace.member.dto.MemberDetailResponse;
import com.server.pickplace.member.dto.MemberListResponse;
import lombok.extern.slf4j.Slf4j;
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
public class MemberService {

	private final MemberRepository memberRepository;

	public MemberSaveResponse addMember(MemberSaveRequest memberSaveRequest) {
		final Member findMember = memberRepository.findByUserId(memberSaveRequest.getUserId());

		if (findMember != null) {
			log.error(findMember.getName());
			throw new MemberException(MemberErrorResult.DUPLICATED_MEMBER_REGISTER);
		}

		Member member = memberRepository.save(Member.builder()
			.userId(memberSaveRequest.getUserId())
			.name(memberSaveRequest.getName())
			.build());

		return MemberSaveResponse.builder()
			.id(member.getId())
			.userId(member.getUserId())
			.name(member.getName())
			.build();
	}

	public List<MemberListResponse> getMemberListByName(String name) {
		List<Member> memberList = memberRepository.findByName(name);

		return memberList.stream()
				.map(member -> MemberListResponse.builder()
						.id(member.getId())
						.userId(member.getUserId())
						.name(member.getName())
						.build())
				.collect(Collectors.toList());
	}

	public MemberDetailResponse getMember(Long id) {
		final Member member = memberRepository.findById(id).orElseThrow(() -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
		return MemberDetailResponse.builder()
				.id(member.getId())
				.name(member.getName())
				.userId(member.getUserId())
				.build();
	}

    public void deleteMember(Long id) {
		memberRepository.findById(id).orElseThrow(() -> new MemberException(MemberErrorResult.MEMBER_NOT_FOUND));
		memberRepository.deleteById(id);
    }
}
