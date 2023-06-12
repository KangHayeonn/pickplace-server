package com.server.pickplace.member.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.pickplace.common.dto.ListResponse;
import com.server.pickplace.common.dto.SingleResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.member.dto.MemberDetailResponse;
import com.server.pickplace.member.dto.MemberListResponse;
import com.server.pickplace.member.dto.MemberSaveRequest;
import com.server.pickplace.member.dto.MemberSaveResponse;
import com.server.pickplace.member.service.MemberService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * description    :
 * packageName    : com.server.pickplace.member.controller
 * fileName       : MemberController
 * author         : tkfdk
 * date           : 2023-05-28
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-05-28        tkfdk       최초 생성
 */
@Tag(name = "1. Member", description = "Member API")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/member")
public class MemberController {
	private final ResponseService responseService;
	private final MemberService memberService;

	@ApiOperation(tags = "1. Member", value = "회원 생성", notes = "회원을 생성한다.")
	@PostMapping("")
	public ResponseEntity<SingleResponse<MemberSaveResponse>> createMember(
		@Valid @RequestBody MemberSaveRequest memberSaveRequest) {
		final MemberSaveResponse memberSaveResponse = memberService.addMember(memberSaveRequest);

		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.CREATED.value(), memberSaveResponse));
	}

	@ApiOperation(tags = "1. Member", value = "회원 리스트 조회", notes = "이름으로 회원들을 조회한다.")
	@GetMapping("")
	public ResponseEntity<ListResponse<MemberListResponse>> findMemberByName(
		@RequestParam(name = "name") String name) {
		return ResponseEntity.ok(
			responseService.getListResponse(HttpStatus.CREATED.value(), memberService.getMemberListByName(name)));
	}

	@ApiOperation(tags = "1. Member", value = "회원 상세 조회", notes = "고유 아이디로 회원을 상세 조회한다.")
	@GetMapping("/{id}")
	public ResponseEntity<MemberDetailResponse> findMemberByName(@PathVariable(name = "id") Long id) {
		return ResponseEntity.ok(memberService.getMember(id));
	}

	@ApiOperation(tags = "1. Member", value = "회원 삭제", notes = "고유 아이디로 회원을 삭제한다.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMemberByName(@PathVariable(name = "id") Long id) {
		memberService.deleteMember(id);
		return ResponseEntity.noContent().build();
	}
}
