package com.server.pickplace.member.controller;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.server.pickplace.member.dto.JwtRequestDto;
import com.server.pickplace.member.dto.MemberSignupRequestDto;
import com.server.pickplace.member.dto.*;
import com.server.pickplace.member.error.MemberErrorResult;
import com.server.pickplace.member.error.MemberException;
import com.server.pickplace.member.repository.MemberRepository;
import com.server.pickplace.member.service.jwt.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.Errors;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.member.service.MemberService;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.util.Base64.getUrlDecoder;

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
	private final MemberRepository memberRepository;;
	private final JwtTokenProvider jwtTokenProvider;

	@Value("${jwt.secret}")
	String secretKey;


	@ApiOperation(tags = "1. Member", value = "로그인", notes = "로그인 시도한다")
	@PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity login(HttpServletRequest httpServletRequest, @RequestBody @Valid JwtRequestDto jwtRequestDto, Errors errors) throws Exception {
//		String email = jwtRequestDto.getEmail();
//		String password = jwtRequestDto.getPassword();

		if(errors.hasErrors()){
			throw new MemberException(MemberErrorResult.HAS_NULL); //null 값 예외 처리
		}

		Map<String, Object> loginResponseDto = memberService.login(httpServletRequest,jwtRequestDto);
		return 	ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), loginResponseDto)); // 성공
	}

	@ApiOperation(tags = "1. Member", value = "회원가입", notes = "회원가입 시도한다")
	@PostMapping(value = "signup", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity signUp(@RequestBody @Valid MemberSignupRequestDto request , Errors errors) throws Exception{

		String signUpResponse = memberService.signup(request);


		if(errors.hasErrors()) {
			String errorDetail = errors.getFieldErrors().toString();
			Map<String, String> validateMap = new HashMap<>();

			// 회원가입 실패시 message 값들을 매핑
			for (FieldError error : errors.getFieldErrors()) {
				String validKeyName = error.getField();
				validateMap.put(validKeyName, error.getDefaultMessage());
				if (validKeyName.equals("email")){
					throw new MemberException(MemberErrorResult.NOT_EMAIL); // 이메일 형식 안지켜진 경우
				}
				else {
					throw new MemberException(MemberErrorResult.HAS_NULL); // null 값인 경우 + 길이 제한 오류
				}
			}
		}
		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(),"회원가입 성공")); // 성공
	}

	@ApiOperation(tags = "1. Member", value = "이메일 중복 체크", notes = "이메일 충복 체크 한다")
	@PostMapping(value = "emailCheck", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity emailCheck(@RequestBody @Valid EmailCheckRequestDto email, Errors errors) {
		Boolean emailCheckResponse = memberService.emailCheck(email);

		if(errors.hasErrors()) {
			String errorDetail = errors.getFieldErrors().toString();

			Map<String, String> validateMap = new HashMap<>();

			for (FieldError error : errors.getFieldErrors()) {
				String validKeyName = error.getField();
				validateMap.put(validKeyName, error.getDefaultMessage());
				System.out.println(validateMap);
				if (error.getDefaultMessage().equals("공백")){
					throw new MemberException(MemberErrorResult.HAS_NULL); //공백
				}
				else {
					throw new MemberException(MemberErrorResult.NOT_EMAIL); // 형식
				}
			}
		}

		System.out.println(emailCheckResponse);
		if (emailCheckResponse==true)
			return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(),"이메일 사용 가능"));
		else {
			throw new MemberException(MemberErrorResult.DUPLICATED_EMAIL); // 중복
		}

	}

	@ApiOperation(tags = "1. Member", value = "토큰 재발급", notes = "토큰을 재발급한다")
	@PostMapping(value = "reissue", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity reissue(HttpServletRequest httpServletRequest, @RequestBody ReissueRequestDto requestDto) {

		String token = jwtTokenProvider.resolveToken((HttpServletRequest) httpServletRequest);

		String payloadJWT = token.split("\\.")[1];
		Base64.Decoder decoder = getUrlDecoder();

		String payload = new String(decoder.decode(payloadJWT));
		JsonParser jsonParser = new BasicJsonParser();
		Map<String, Object> jsonArray = jsonParser.parseMap(payload);
		String id = (String) jsonArray.get("sub");

		memberService.reissue(id,requestDto);



		Map<String, Object> reissueResponseDto = memberService.reissue(id,requestDto);
		return 	ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), reissueResponseDto)); // 성공
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
