package com.server.pickplace.member.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * description    :
 * packageName    : com.server.pickplace.member.error
 * fileName       : MemberErrorResult
 * author         : tkfdk
 * date           : 2023-05-29
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-05-29        tkfdk       최초 생성
 */
@Getter
@RequiredArgsConstructor
public enum MemberErrorResult {
	DUPLICATED_MEMBER_REGISTER(HttpStatus.BAD_REQUEST, "Duplicated Member Register Request"),
	UNKNOWN_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Exception"),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,"Member Not Found");

	private final HttpStatus httpStatus;
	private final String message;
}
