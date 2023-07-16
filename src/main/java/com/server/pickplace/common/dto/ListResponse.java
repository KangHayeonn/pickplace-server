package com.server.pickplace.common.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * description    :
 * packageName    : com.server.pickplace.common.dto
 * fileName       : ListResponse
 * author         : tkfdk
 * date           : 2023-06-09
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-09        tkfdk       최초 생성
 */
@Getter
public class ListResponse<T> extends CommonResponse {

	private List<T> data;

	public ListResponse(boolean success, int code, List<T> data) {
		super(success, code);
		this.data = data;
	}
}
