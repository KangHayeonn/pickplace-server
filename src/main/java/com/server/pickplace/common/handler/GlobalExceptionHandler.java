
package com.server.pickplace.common.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.server.pickplace.common.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.server.pickplace.member.error.MemberErrorResult;
import com.server.pickplace.member.error.MemberException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * description    :
 * packageName    : com.server.pickplace.common
 * fileName       : GlobalExceptionHandler
 * author         : tkfdk
 * date           : 2023-05-30
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-05-30        tkfdk       최초 생성
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired ResponseService responseService;

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		final MethodArgumentNotValidException ex,
		final HttpHeaders headers,
		final HttpStatus status,
		final WebRequest request) {

		final List<String> errorList = ex.getBindingResult()
			.getAllErrors()
			.stream()
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.collect(Collectors.toList());

		Map<String, Object> validErrorResponseMap = new HashMap<>();

		return ResponseEntity.ok(responseService.getSingleResponse(HttpStatus.OK.value(), new Object()));
	}


	@ExceptionHandler({MemberException.class})
	public ResponseEntity<ErrorResponse> handleRestApiException(final MemberException exception) {
		log.warn("MemberException occur: ", exception);
		return this.makeErrorResponseEntity(exception.getErrorResult());
	}


//	@ExceptionHandler({Exception.class})
//	public ResponseEntity handleException(final Exception exception) {
//
//		return ResponseEntity.ok(responseService.getErrorResponse(HttpStatus.OK.value(), "알 수 없는 오류입니다."));
//
//	}

	private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final MemberErrorResult errorResult) {
		return ResponseEntity.status(errorResult.getHttpStatus())
			.body(new ErrorResponse(errorResult.name(), errorResult.getMessage()));
	}

	@Getter
	@RequiredArgsConstructor
	static class ErrorResponse {
		private final String code;
		private final String message;
	}

}

