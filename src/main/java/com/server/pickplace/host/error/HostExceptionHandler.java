package com.server.pickplace.host.error;


import com.server.pickplace.common.dto.ErrorResponse;
import com.server.pickplace.common.service.ResponseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class HostExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired ResponseService responseService;
    @ExceptionHandler({HostException.class})
    public ResponseEntity<ErrorResponse> hostException(final HostException exception) {

        ErrorResponse errorResponse = getErrorResponse(exception);

        return ResponseEntity.ok(errorResponse);

    }

    private ErrorResponse getErrorResponse(HostException exception) {
        int code = exception.getErrorResult().getHttpStatus().value();
        String msg = exception.getErrorResult().getMessage();

        ErrorResponse errorResponse = responseService.getErrorResponse(code, msg);

        return errorResponse;
    }



}
