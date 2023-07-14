package com.server.pickplace.member.error;

import com.server.pickplace.common.dto.ErrorResponse;
import com.server.pickplace.common.service.ResponseService;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

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
	UNKNOWN_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Exception1111"),
	HAS_NULL(HttpStatus.BAD_REQUEST, "올바른 값을 입력해주세요"),
	NOT_EMAIL(HttpStatus.BAD_REQUEST, "이메일 형식이 지켜지지 않았습니다."),
	DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이메일 중복입니다"),
	MEMBER_NOT_ID(HttpStatus.NOT_FOUND,"존재하지 않는 아이디 입니다"),
	MEMBER_NOT_PW(HttpStatus.NOT_FOUND,"비밀번호가 틀렸습니다"),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,"member not found");


	private final HttpStatus httpStatus;
	private final String message;



}
