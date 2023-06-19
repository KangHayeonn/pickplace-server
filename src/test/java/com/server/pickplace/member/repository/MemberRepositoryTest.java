package com.server.pickplace.member.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import com.server.pickplace.member.dto.MemberSaveRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.server.pickplace.member.entity.Member;

import java.util.List;

/**
 * description    :
 * packageName    : com.server.pickplace.member.repository
 * fileName       : MemberRepositoryTest
 * author         : tkfdk
 * date           : 2023-05-29
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-05-29        tkfdk       최초 생성
 */
@DataJpaTest
@Transactional
class MemberRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	@Test
	public void 멤버등록() {
		// given
		final Member member = Member.builder()
			.email("email")
			.name("김선웅")
			.build();

		// when
		final Member memberResult = memberRepository.save(member);

		// then
		assertThat(member.getEmail()).isEqualTo(memberResult.getEmail());
		assertThat(member.getName()).isEqualTo(memberResult.getName());
	}

	@Test
	public void 유저아이디로조회() {
		// given
		final Member member = Member.builder()
			.email("email")
			.name("김선웅")
			.build();

		// when
		memberRepository.save(member);
		final Member memberResult = memberRepository.findByEmail("email");

		// then
		assertThat(memberResult.getEmail()).isEqualTo("email");
		assertThat(memberResult.getName()).isEqualTo(member.getName());
	}

	@Test
	public void 유저조회_사이즈0() {
		// given

		// when
		List<Member> result = memberRepository.findByName("홍길동");

		// then
		assertThat(result.size()).isEqualTo(0);
	}

	@Test
	public void 유저조회_사이즈2() {
		// given
		Member member = Member.builder()
				.email("email")
				.name("홍길동")
				.build();

		Member member2 = Member.builder()
				.email("email2")
				.name("홍길동")
				.build();
		memberRepository.save(member);
		memberRepository.save(member2);

		// when
		List<Member> result = memberRepository.findByName("홍길동");

		// then
		assertThat(result.size()).isEqualTo(2);
	}

	@Test
	public void 유저추가후삭제() {
		// given
		Member memberSaveRequest = Member.builder()
				.email("email")
				.name("홍길동")
				.build();
		final Member member = memberRepository.save(memberSaveRequest);

		// when
		memberRepository.deleteById(member.getId());

		// then

	}
}