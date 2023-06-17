package com.server.pickplace.member.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.server.pickplace.member.dto.MemberDetailResponse;
import com.server.pickplace.member.dto.MemberListResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.server.pickplace.member.dto.MemberSaveRequest;
import com.server.pickplace.member.dto.MemberSaveResponse;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.error.MemberErrorResult;
import com.server.pickplace.member.error.MemberException;
import com.server.pickplace.member.repository.MemberRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * description    :
 * packageName    : com.server.pickplace.user.service
 * fileName       : UserServiceTest
 * author         : tkfdk
 * date           : 2023-05-29
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-05-29        tkfdk       최초 생성
 */
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

	private final String email = "email";
	private final String name = "김선웅";

	@InjectMocks
	private MemberService memberService;
	@Mock
	private MemberRepository memberRepository;

	@Test
	public void 유저등록실패_이미존재함() {
		// given
		doReturn(Member.builder().build()).when(memberRepository).findByEmail(email);

		// when
		final MemberException result = assertThrows(MemberException.class,
			() -> memberService.addMember(memberSaveRequest()));

		// then
		assertThat(result.getErrorResult()).isEqualTo(MemberErrorResult.DUPLICATED_MEMBER_REGISTER);

	}

	@Test
	public void 유저등록성공() {
		// given
		doReturn(null).when(memberRepository).findByEmail(email);
		doReturn(member()).when(memberRepository).save(any(Member.class));

		// when
		final MemberSaveResponse result = memberService.addMember(memberSaveRequest());

		// then
		assertThat(result.getId()).isNotNull();
		assertThat(result.getEmail()).isEqualTo(email);

		// verify
		verify(memberRepository, times(1)).findByEmail(email);
		verify(memberRepository, times(1)).save(any(Member.class));

	}

	@Test
	public void 유저목록조회() {
		// given
		doReturn(Arrays.asList(
				Member.builder().build(),
				Member.builder().build(),
				Member.builder().build()
		)).when(memberRepository).findByName(name);

		// when
		final List<MemberListResponse> result = memberService.getMemberListByName(name);

		// then
		assertThat(result.size()).isEqualTo(3);

	}

	@Test
	public void 유저상세조회실패_멤버가존재하지않음() {
		// given
		Long id = 1L;
		doReturn(Optional.empty()).when(memberRepository).findById(anyLong());

		// when
		final MemberException result = assertThrows(MemberException.class, () -> memberService.getMember(id));

		// then
		assertThat(result.getErrorResult()).isEqualTo(MemberErrorResult.MEMBER_NOT_FOUND);
	}

	@Test
	public void 유저상세조회성공() {
		// given
		Long id = -1L;
		doReturn(Optional.of(member())).when(memberRepository).findById(anyLong());

		// when
		final MemberDetailResponse result = memberService.getMember(id);

		// then
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getName()).isEqualTo(name);
	}

	@Test
	public void 유저삭제실패_존재하지않음() {
		// given
		Long id = 1L;
		doReturn(Optional.empty()).when(memberRepository).findById(anyLong());

		// when
		final MemberException result = assertThrows(MemberException.class, () -> memberService.deleteMember(id));

		// then
		assertThat(result.getErrorResult()).isEqualTo(MemberErrorResult.MEMBER_NOT_FOUND);
	}

	@Test
	public void 유저삭제성공() {
		// given
		Long id = -1L;
		doReturn(Optional.of(member())).when(memberRepository).findById(id);

		// when
		memberService.deleteMember(id);
	}

	private Member member() {
		return Member.builder()
			.id(-1L)
			.email(email)
			.name(name)
			.build();
	}

	private MemberSaveRequest memberSaveRequest() {
		return MemberSaveRequest.builder()
			.email(email)
			.name(name)
			.build();
	}
}