
package com.server.pickplace.common.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.server.pickplace.common.dto.ErrorResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.host.error.HostException;
import com.server.pickplace.reservation.error.ReservationException;
import com.server.pickplace.review.error.ReviewException;
import com.server.pickplace.search.error.SearchException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
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

		String errorMessage = errorList.get(0);

		return ResponseEntity.ok(responseService.getErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage));
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.ok(responseService.getErrorResponse(status.value(), "형식이 일치하지 않는 데이터가 존재합니다."));
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.ok(responseService.getErrorResponse(status.value(), "지원하지 않는 HTTP 메서드입니다."));
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.ok(responseService.getErrorResponse(status.value(), "지원하지 않는 HTTP 콘텐츠 타입입니다."));
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.ok(responseService.getErrorResponse(status.value(), "지원하지 않는 HTTP 액세스 타입입니다."));
	}

	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.ok(responseService.getErrorResponse(status.value(), "경로변수가 존재하지 않습니다."));
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.ok(responseService.getErrorResponse(status.value(), "요청 파라미터가 존재하지 않습니다."));
	}

	@Override
	protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.ok(responseService.getErrorResponse(status.value(), "서버 내부 바인딩 에러입니다."));
	}

	@Override
	protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.ok(responseService.getErrorResponse(status.value(), "서버 내부 타입 변환 에러입니다."));
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.ok(responseService.getErrorResponse(status.value(), "매개변수 혹은 경로 변수 타입이 일치하지 않습니다."));
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.ok(responseService.getErrorResponse(status.value(), "서버 내부 Response 형식 변환 에러입니다."));
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.ok(responseService.getErrorResponse(status.value(), "매개변수 혹은 경로 변수 타입이 일치하지 않습니다."));
	}


	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.ok(responseService.getErrorResponse(status.value(), "잘못된 경로의 요청입니다."));
	}

	@Override
	protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
		return ResponseEntity.ok(responseService.getErrorResponse(status.value(), "타임아웃 에러입니다."));
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.ok(responseService.getErrorResponse(status.value(), "알 수 없는 오류입니다."));
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<ErrorResponse> handleException(final Exception exception) {

		log.info("Exception occurs : {}", exception.getStackTrace());

		return ResponseEntity.ok(responseService.getErrorResponse(HttpStatus.OK.value(), "알 수 없는 오류입니다."));

	}

	@ExceptionHandler({SearchException.class})
	public ResponseEntity<ErrorResponse> searchException(final SearchException exception) {

		ErrorResponse errorResponse = getErrorResponse(exception);

		return ResponseEntity.ok(errorResponse);

	}


	@ExceptionHandler({ReservationException.class})
	public ResponseEntity<ErrorResponse> hostException(final ReservationException exception) {

		ErrorResponse errorResponse = getErrorResponse(exception);

		return ResponseEntity.ok(errorResponse);

	}

	@ExceptionHandler({HostException.class})
	public ResponseEntity<ErrorResponse> hostException(final HostException exception) {

		ErrorResponse errorResponse = getErrorResponse(exception);

		return ResponseEntity.ok(errorResponse);

	}

	@ExceptionHandler({ReviewException.class})
	public ResponseEntity<ErrorResponse> reviewException(final ReviewException exception) {

		ErrorResponse errorResponse = getErrorResponse(exception);

		return ResponseEntity.ok(errorResponse);

	}

	private ErrorResponse getErrorResponse(ReviewException exception) {

		int code = exception.getErrorResult().getHttpStatus().value();
		String msg = exception.getErrorResult().getMessage();

		ErrorResponse errorResponse = responseService.getErrorResponse(code, msg);

		return errorResponse;

	}


	private ErrorResponse getErrorResponse(HostException exception) {
		int code = exception.getErrorResult().getHttpStatus().value();
		String msg = exception.getErrorResult().getMessage();

		ErrorResponse errorResponse = responseService.getErrorResponse(code, msg);

		return errorResponse;
	}


	private ErrorResponse getErrorResponse(ReservationException exception) {

		int code = exception.getErrorResult().getHttpStatus().value();
		String msg = exception.getErrorResult().getMessage();

		ErrorResponse errorResponse = responseService.getErrorResponse(code, msg);

		return errorResponse;

	}

	private ErrorResponse getErrorResponse(SearchException exception) {

		int code = exception.getErrorResult().getHttpStatus().value();
		String msg = exception.getErrorResult().getMessage();

		ErrorResponse errorResponse = responseService.getErrorResponse(code, msg);

		return errorResponse;

	}







}

