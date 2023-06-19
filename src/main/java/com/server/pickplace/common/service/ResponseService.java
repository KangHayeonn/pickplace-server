package com.server.pickplace.common.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.server.pickplace.common.dto.ErrorResponse;
import com.server.pickplace.common.dto.ListResponse;
import com.server.pickplace.common.dto.SingleResponse;

/**
 * description    :
 * packageName    : com.server.pickplace.common
 * fileName       : ResponseService
 * author         : tkfdk
 * date           : 2023-06-04
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-04        tkfdk       최초 생성
 */
@Service
public class ResponseService {

	public ErrorResponse getErrorResponse(int code, String errMsg) {
		return new ErrorResponse(false, code, errMsg);
	}

	public <T> SingleResponse<T> getSingleResponse(int code, T data) {
		return new SingleResponse<>(true, code, data);
	}

	public <T> ListResponse<T> getListResponse(int code, List<T> data) {
		return new ListResponse<>(true, code, data);
	}

}