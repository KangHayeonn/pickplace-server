package com.server.pickplace.member.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * description    :
 * packageName    : com.server.pickplace.member.error
 * fileName       : MemberException
 * author         : tkfdk
 * date           : 2023-05-29
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-05-29        tkfdk       최초 생성
 */
@Getter
@RequiredArgsConstructor
public class MemberException extends RuntimeException {
	private final MemberErrorResult errorResult;
}
