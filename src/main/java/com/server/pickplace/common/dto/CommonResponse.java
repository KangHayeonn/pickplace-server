package com.server.pickplace.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * description    :
 * packageName    : com.server.pickplace.common.dto
 * fileName       : CommonResponse
 * author         : tkfdk
 * date           : 2023-06-04
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-04        tkfdk       최초 생성
 */
@Getter
@AllArgsConstructor
public class CommonResponse {
	@ApiModelProperty(value = "응답 성공여부 : true/false")
	private boolean success;
	@ApiModelProperty(value = "응답 코드 번호")
	private int code;
}
