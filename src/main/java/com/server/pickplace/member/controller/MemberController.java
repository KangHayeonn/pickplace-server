package com.server.pickplace.member.controller;

import javax.validation.Valid;

import com.server.pickplace.auth.dto.JwtRequestDto;
import com.server.pickplace.auth.dto.TokenInfo;
import com.server.pickplace.member.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping(value = "/api/v1/members")
//@AllArgsConstructor
public class MemberController {
//	private final ResponseService responseService;
	private final MemberService memberService;
	private final ResponseService responseService;

	@GetMapping(value = "login")
	public String signup() {
		return "login";
	}

	@ApiOperation(tags = "1. Member", value = "로그인", notes = "로그인 성공")
	@PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity login(@RequestBody JwtRequestDto jwtRequestDto) throws Exception {
//		String email = jwtRequestDto.getEmail();
//		String password = jwtRequestDto.getPassword();
		LoginResponseDto loginResponseDto = memberService.login(jwtRequestDto);
		return 		ResponseEntity.ok(responseService.getListResponse(HttpStatus.OK.value(), loginResponseDto))
		;
	}


//	@ApiOperation(tags = "1. Member", value = "회원 생성", notes = "회원을 생성한다.")
//	@PostMapping("")
//	public ResponseEntity<SingleResponse<MemberSaveResponse>> createMember(
//		@Valid @RequestBody MemberSaveRequest memberSaveRequest) {
//		final MemberSaveResponse memberSaveResponse = memberService.addMember(memberSaveRequest);
//
//		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.CREATED.value(), memberSaveResponse));
//	}
//
//	@ApiOperation(tags = "1. Member", value = "회원 리스트 조회", notes = "이름으로 회원들을 조회한다.")
//	@GetMapping("")
//	public ResponseEntity<ListResponse<MemberListResponse>> findMemberByName(
//		@RequestParam(name = "name") String name) {
//		return ResponseEntity.ok(
//			responseService.getListResponse(HttpStatus.CREATED.value(), memberService.getMemberListByName(name)));
//	}
//
//	@ApiOperation(tags = "1. Member", value = "회원 상세 조회", notes = "고유 아이디로 회원을 상세 조회한다.")
//	@GetMapping("/{id}")
//	public ResponseEntity<MemberDetailResponse> findMemberByName(@PathVariable(name = "id") Long id) {
//		return ResponseEntity.ok(memberService.getMember(id));
//	}
//
//	@ApiOperation(tags = "1. Member", value = "회원 삭제", notes = "고유 아이디로 회원을 삭제한다.")
//	@DeleteMapping("/{id}")
//	public ResponseEntity<Void> deleteMemberByName(@PathVariable(name = "id") Long id) {
//		memberService.deleteMember(id);
//		return ResponseEntity.noContent().build();
//	}
}
