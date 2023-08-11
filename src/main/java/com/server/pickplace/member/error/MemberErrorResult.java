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
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 회원입니다"),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"만료된 token 입니다"),
	UNKNOWN_TOKEN(HttpStatus.NOT_FOUND,"잘못된 refresh token 입니다"),
	INVALID_TOKEN(HttpStatus.NOT_FOUND,"존재하지 않는 access token 입니다"),
	NOT_AUTHENTICATION(HttpStatus.NOT_FOUND,"권한이 없습니다"),
	ALREADY_LOGOUT(HttpStatus.NOT_FOUND,"이미 로그아웃 되어있습니다"),
	NOT_KAKAO(HttpStatus.NOT_FOUND,"카카오 사용자는 비밀번호 변경이 불가능 합니다"),

	//my page error
	NOT_RESERVATION(HttpStatus.NOT_FOUND,"예약 내역이 없습니다");


	private final HttpStatus httpStatus;
	private final String message;



}
