package com.server.pickplace.member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.server.pickplace.member.dto.JwtRequestDto;
import com.server.pickplace.member.dto.MemberSignupRequestDto;
import com.server.pickplace.member.dto.*;
import com.server.pickplace.member.error.MemberErrorResult;
import com.server.pickplace.member.error.MemberException;
import com.server.pickplace.member.repository.MemberRepository;
import com.server.pickplace.member.service.MemberInfoService;
import com.server.pickplace.member.service.jwt.JwtTokenProvider;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.Errors;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.member.service.MemberService;

import io.swagger.annotations.ApiOperation;
//import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

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
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/members")
//@AllArgsConstructor
public class MemberController {
	//	private final ResponseService responseService;
	private final MemberService memberService;
	private final MemberInfoService memberInfoService;
	private final ResponseService responseService;
	private final MemberRepository memberRepository;

	private final JwtTokenProvider jwtTokenProvider;



	@Value("${jwt.secret}")
	String secretKey;


	@ApiOperation(tags = "1. Member", value = "로그인", notes = "로그인 시도한다!!")
	@PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity login(@ApiIgnore HttpServletRequest httpServletRequest, @ApiParam(required = true) @RequestBody @Valid JwtRequestDto jwtRequestDto, @ApiIgnore Errors errors) throws Exception {

		if (errors.hasErrors()) {
			throw new MemberException(MemberErrorResult.HAS_NULL); //null 값 예외 처리
		}

		Map<String, Object> loginResponseDto = memberService.login(httpServletRequest, jwtRequestDto);
		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), loginResponseDto)); // 성공
	}

	@ApiOperation(tags = "1. Member", value = "회원가입", notes = "회원가입 시도한다!")
	@PostMapping(value = "signup", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity signUp(@RequestBody @Valid MemberSignupRequestDto request, @ApiIgnore Errors errors) throws Exception {

		if (errors.hasErrors()) {
			String errorDetail = errors.getFieldErrors().toString();
			Map<String, String> validateMap = new HashMap<>();

			// 회원가입 실패시 message 값들을 매핑
			for (FieldError error : errors.getFieldErrors()) {
				String validKeyName = error.getField();
				validateMap.put(validKeyName, error.getDefaultMessage());
				if (validKeyName.equals("email")) {
					throw new MemberException(MemberErrorResult.NOT_EMAIL); // 이메일 형식 안지켜진 경우
				} else {
					throw new MemberException(MemberErrorResult.HAS_NULL); // null 값인 경우 + 길이 제한 오류
				}
			}
		}

		String signUpResponse = memberService.signup(request);


		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), "회원가입 성공")); // 성공
	}

	@ApiOperation(tags = "1. Member", value = "이메일 중복 체크", notes = "이메일 중복 체크 한다")
	@PostMapping(value = "emailCheck", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity emailCheck(@RequestBody @Valid EmailCheckRequestDto email, @ApiIgnore Errors errors) {

		if (errors.hasErrors()) {
			String errorDetail = errors.getFieldErrors().toString();

			Map<String, String> validateMap = new HashMap<>();

			for (FieldError error : errors.getFieldErrors()) {
				String validKeyName = error.getField();
				validateMap.put(validKeyName, error.getDefaultMessage());
				if (error.getDefaultMessage().equals("공백")) {
					throw new MemberException(MemberErrorResult.HAS_NULL); //공백
				} else {
					throw new MemberException(MemberErrorResult.NOT_EMAIL); // 형식
				}
			}
		}

		Boolean emailCheckResponse = memberService.emailCheck(email);

		if (emailCheckResponse == true)
			return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), "이메일 사용 가능"));
		else {
			throw new MemberException(MemberErrorResult.DUPLICATED_EMAIL); // 중복
		}

	}

	@ApiOperation(tags = "1. Member", value = "토큰 재발급", notes = "토큰을 재발급한다.")
	@PostMapping(value = "reissue", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity reissue(@ApiIgnore HttpServletRequest httpServletRequest, @RequestBody ReissueRequestDto requestDto) {

		String token = jwtTokenProvider.resolveToken((HttpServletRequest) httpServletRequest);

		String payloadJWT = token.split("\\.")[1];
		Base64.Decoder decoder = getUrlDecoder();

		String payload = new String(decoder.decode(payloadJWT));
		JsonParser jsonParser = new BasicJsonParser();
		Map<String, Object> jsonArray = jsonParser.parseMap(payload);
		String id = (String) jsonArray.get("sub");

		memberService.reissue(id, requestDto);


		Map<String, Object> reissueResponseDto = memberService.reissue(id, requestDto);
		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), reissueResponseDto)); // 성공
	}

	@ApiOperation(tags = "1. Member", value = "로그아웃", notes = "로그아웃 한다")
	@GetMapping(value = "logout")
	public ResponseEntity logout(HttpServletRequest request) {

		memberService.logout(request);

		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), "로그아웃"));
	}

	@ApiOperation(tags = "1. Member", value = "회원 탈퇴 ", notes = "회원 탈퇴한다")
	@DeleteMapping("/")
	public ResponseEntity putNicknameInfo(@ApiIgnore HttpServletRequest httpServletRequest, @RequestBody MemberIdRequestDto memberId) {

//		memberInfoService.checkInfoValid(httpServletRequest, memberId.getMemberId()); // 토큰 만료 , 존재하지 않는 회원 , 권한없음 처리
		memberService.logout(httpServletRequest); //로그아웃 후 회원 탈퇴 진행 (토큰 삭제 과정이라서)
		memberService.deleteMember(memberId.getMemberId());
		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), "탈퇴"));
	}

	@ApiOperation(tags = "1. Member", value = "내 정보 조회", notes = "내 정보 조회한다")
	@GetMapping("info/{memberId}")
	public ResponseEntity getInfo(@ApiIgnore HttpServletRequest httpServletRequest, @PathVariable Long memberId) {

		Map<String, Object> infoResponseDto = memberInfoService.info(httpServletRequest, memberId);
		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), infoResponseDto)); // 성공
	}

	@ApiOperation(tags = "1. Member", value = "내 정보 수정 - 전화번호 ", notes = "내 정보 : 전화번호를 수정한다")
	@PutMapping("info/phone")
	public ResponseEntity putPhoneInfo(@ApiIgnore HttpServletRequest httpServletRequest, @RequestBody InfoPhoneRequestDto requestDto) {

		memberInfoService.phoneUpdate(httpServletRequest, requestDto);
		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), "비밀번호 수정 완료"));
	}

	@ApiOperation(tags = "1. Member", value = "내 정보 수정 - 닉네임 ", notes = "내 정보 : 내 닉네임을 수정한다")
	@PutMapping("info/nickname")
	public ResponseEntity putNicknameInfo(@ApiIgnore HttpServletRequest httpServletRequest, @RequestBody InfoNicknameRequestDto requestDto) {

		memberInfoService.nicknameUpdate(httpServletRequest, requestDto);
		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), "닉네임 수정 완료"));
	}


}