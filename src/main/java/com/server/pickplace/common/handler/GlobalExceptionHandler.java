package com.server.pickplace.common.handler;

import java.util.List;
import java.util.stream.Collectors;

import com.server.pickplace.common.dto.ErrorResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.host.error.HostException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
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

		log.warn("Invalid DTO Parameter errors : {}", errorList);
		return this.makeErrorResponseEntity(errorList.toString());
	}

	private ResponseEntity<Object> makeErrorResponseEntity(final String errorDescription) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new staticErrorResponse(HttpStatus.BAD_REQUEST.toString(), errorDescription));
	}

	@ExceptionHandler({MemberException.class})
	public ResponseEntity<staticErrorResponse> handleRestApiException(final MemberException exception) {
		log.warn("MemberException occur: ", exception);
		return this.makeErrorResponseEntity(exception.getErrorResult());
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<staticErrorResponse> handleException(final Exception exception) {
		log.warn("Exception occur: ", exception);
		return this.makeErrorResponseEntity(MemberErrorResult.UNKNOWN_EXCEPTION);
	}

	private ResponseEntity<staticErrorResponse> makeErrorResponseEntity(final MemberErrorResult errorResult) {
		return ResponseEntity.status(errorResult.getHttpStatus())
			.body(new staticErrorResponse(errorResult.name(), errorResult.getMessage()));
	}
	@Getter
	@RequiredArgsConstructor
	static class staticErrorResponse {
		private final String code;
		private final String message;
	}
}
