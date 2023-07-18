package com.server.pickplace.member.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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
public class MemberExceptionHandler extends ResponseEntityExceptionHandler{

        @Autowired
        ResponseService responseService;
        @ExceptionHandler({MemberException.class})
        public @ResponseBody ResponseEntity<ErrorResponse> memberException(final MemberException exception) {
            ErrorResponse errorResponse = getErrorResponse(exception);
            return ResponseEntity.ok(errorResponse);
        }

        private ErrorResponse getErrorResponse(MemberException exception) {
            int code = exception.getErrorResult().getHttpStatus().value();
            String msg = exception.getErrorResult().getMessage();

            ErrorResponse errorResponse = responseService.getErrorResponse(code, msg);

            return errorResponse;
        }



    }
