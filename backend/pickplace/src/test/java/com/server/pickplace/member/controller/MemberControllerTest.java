package com.server.pickplace.member.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.gson.Gson;
import com.server.pickplace.common.handler.GlobalExceptionHandler;
import com.server.pickplace.member.dto.MemberDetailResponse;
import com.server.pickplace.member.dto.MemberListResponse;
import com.server.pickplace.member.dto.MemberSaveRequest;
import com.server.pickplace.member.dto.MemberSaveResponse;
import com.server.pickplace.member.error.MemberErrorResult;
import com.server.pickplace.member.error.MemberException;
import com.server.pickplace.member.service.MemberService;

/**
 * description    :
 * packageName    : com.server.pickplace.member.controller
 * fileName       : MemberControllerTest
 * author         : tkfdk
 * date           : 2023-05-30
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-05-30        tkfdk       최초 생성
 */
@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {

	private final static String email = "email";
	private final static String name = "김선웅";

	@InjectMocks
	private MemberController memberController;
	@Mock
	private MemberService memberService;

	private MockMvc mockMvc;
	private Gson gson;

	@BeforeEach
	public void beforeEach() {
		gson = new Gson();
		mockMvc = MockMvcBuilders.standaloneSetup(memberController)
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();
	}

	@Test
	public void 멤버등록성공() throws Exception {
		// given
		final String url = "/api/v1/member";
		final MemberSaveResponse memberSaveResponse = MemberSaveResponse.builder()
			.id(-1L).email(email).name(name).build();
		doReturn(memberSaveResponse).when(memberService).addMember(any(MemberSaveRequest.class));
		//		given(memberService.addMember(any(MemberSaveRequest.class))).willReturn(memberSaveResponse);

		// when
		final ResultActions resultActions = mockMvc.perform(
			post(url)
				.content(gson.toJson(memberSaveRequest()))
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isCreated());

		final MemberSaveResponse response = gson.fromJson(resultActions.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8), MemberSaveResponse.class);

		assertThat(response.getEmail()).isEqualTo(email);
		assertThat(response.getName()).isEqualTo(name);
	}

	@Test
	public void 멤버등록실패_MemberService에서에러Throw() throws Exception {
		// given
		final String url = "/api/v1/member";

		doThrow(new MemberException(MemberErrorResult.DUPLICATED_MEMBER_REGISTER))
			.when(memberService)
			.addMember(any(MemberSaveRequest.class));

		// when
		final ResultActions resultActions = mockMvc.perform(
			post(url)
				.content(gson.toJson(memberSaveRequest()))
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isBadRequest());
	}

	@Test
	public void 이름으로_멤버목록조회_성공() throws Exception {
		// given
		final String url = "/api/v1/member";
		doReturn(Arrays.asList(
			MemberListResponse.builder().build(),
			MemberListResponse.builder().build(),
			MemberListResponse.builder().build()
		)).when(memberService).getMemberListByName(name);

		// when
		final ResultActions resultActions = mockMvc.perform(
			get(url)
				.param("name", name)
		);

		// then
		resultActions.andExpect(status().isOk());

	}

	@Test
	public void 멤버상세조회_멤버가존재하지않음() throws Exception {
		// given
		final String url = "/api/v1/member/-1";
		doThrow(new MemberException(MemberErrorResult.MEMBER_NOT_FOUND))
			.when(memberService)
			.getMember(-1L);

		// when
		final ResultActions resultActions = mockMvc.perform(
			get(url)
		);

		// then
		resultActions.andExpect(status().isNotFound());
	}

	@Test
	public void 멤버상세조회성공() throws Exception {
		// given
		final String url = "/api/v1/member/-1";
		doReturn(MemberDetailResponse.builder().build())
			.when(memberService)
			.getMember(-1L);

		// when
		final ResultActions resultActions = mockMvc.perform(
			get(url)
		);

		// then
		resultActions.andExpect(status().isOk());
	}

	@Test
	public void 멤버삭제_멤버가존재하지않음() throws Exception {
		// given
		final String url = "/api/v1/member/-1";
		doThrow(new MemberException(MemberErrorResult.MEMBER_NOT_FOUND))
			.when(memberService)
			.deleteMember(-1L);

		// when
		final ResultActions resultActions = mockMvc.perform(
			delete(url)
		);

		// then
		resultActions.andExpect(status().isNotFound());
	}

	@Test
	public void 멤버삭제성공() throws Exception {
		// given
		final String url = "/api/v1/member/-1";

		// when
		final ResultActions resultActions = mockMvc.perform(
			delete(url)
		);

		// then
		resultActions.andExpect(status().isNoContent());
	}

	private MemberSaveRequest memberSaveRequest() {
		return MemberSaveRequest.builder()
			.email(email)
			.name(name)
			.build();
	}

}