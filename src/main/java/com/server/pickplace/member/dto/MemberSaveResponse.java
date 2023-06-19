package com.server.pickplace.member.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * description    :
 * packageName    : com.server.pickplace.member.dto
 * fileName       : MemberSaveResponse
 * author         : tkfdk
 * date           : 2023-05-30
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-05-30        tkfdk       최초 생성
 */
@Getter
@Builder
public class MemberSaveResponse {
	private Long id;
	private String email;
	private String name;
}
