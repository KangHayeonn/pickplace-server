package com.server.pickplace.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * description    :
 * packageName    : com.server.pickplace.common.dto
 * fileName       : SingleResponse
 * author         : tkfdk
 * date           : 2023-06-09
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-09        tkfdk       최초 생성
 */
@Getter
public class SingleResponse<T> extends CommonResponse {

	private T data;

	public SingleResponse(boolean success, int code, T data) {
		super(success, code);
		this.data = data;
	}
}
