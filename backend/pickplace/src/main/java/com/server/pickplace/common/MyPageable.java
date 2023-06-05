package com.server.pickplace.common;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * description    :
 * packageName    : PACKAGE_NAME
 * fileName       : common.MyPageable
 * author         : tkfdk
 * date           : 2023-05-28
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-05-28        tkfdk       최초 생성
 */
@Getter
@Setter
@ApiModel
public class MyPageable {
	@ApiModelProperty(value = "페이지 번호(0..N)")
	private Integer page;

	@ApiModelProperty(value = "페이지 크기")
	private Integer size;

	@ApiModelProperty(value = "정렬(ASC|DESC)")
	private List<String> sort;
}
