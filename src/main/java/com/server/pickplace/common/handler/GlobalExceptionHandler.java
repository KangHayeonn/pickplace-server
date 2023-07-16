package com.server.pickplace.common.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
		return this.makeErrorResponseEntity(CommonErrorResult.VALIDATION_EXCEPTION);
	}

//	@ExceptionHandler({Exception.class})
//	public ResponseEntity<Object> handleException(final Exception exception) {
//		log.warn("Exception occur: ", exception.getMessage());
//		return this.makeErrorResponseEntity(CommonErrorResult.UNKNOWN_EXCEPTION);
//	}

	@ExceptionHandler({MissingRequestHeaderException.class})
	public ResponseEntity<Object> missingRequestHandleException(final Exception exception) {
		log.warn("Exception occur: ", exception.getMessage());
		return this.makeErrorResponseEntity(CommonErrorResult.MISSING_REQUEST_HEADER);
	}


	private ResponseEntity<Object> makeErrorResponseEntity(final CommonErrorResult errorResult) {
		return ResponseEntity.ok(new CommonErrorResponse(errorResult));
	}




}
