package com.server.pickplace.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.pickplace.member.entity.Member;

import java.util.List;

/**
 * description    :
 * packageName    : com.server.pickplace.member.repository
 * fileName       : MemberRepository
 * author         : tkfdk
 * date           : 2023-05-29
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-05-29        tkfdk       최초 생성
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Member findByUserId(final String userId);
	List<Member> findByName(final String name);
}
