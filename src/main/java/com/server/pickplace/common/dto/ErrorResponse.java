package com.server.pickplace.common.dto;

import lombok.Getter;

/**
 * description    :
 * packageName    : com.server.pickplace.common.dto
 * fileName       : ErrorResponse
 * author         : tkfdk
 * date           : 2023-06-09
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-09        tkfdk       최초 생성
 */
@Getter
public class ErrorResponse extends CommonResponse {
	private String errMsg;

	public ErrorResponse(boolean success, int code, String errMsg) {
		super(success, code);
		this.errMsg = errMsg;
	}
}
