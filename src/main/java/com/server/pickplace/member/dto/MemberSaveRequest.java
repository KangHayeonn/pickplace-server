package com.server.pickplace.member.dto;

import com.sun.istack.NotNull;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * description    :
 * packageName    : com.server.pickplace.member.dto
 * fileName       : MemberSaveRequest
 * author         : tkfdk
 * date           : 2023-05-28
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-05-28        tkfdk       최초 생성
 */
@Getter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class MemberSaveRequest {
	@NotNull
	private final String email;
	@NotNull
	private final String name;
}
