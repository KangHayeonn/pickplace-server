package com.server.pickplace.member.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class MembeExceptionHandler {

    @ExceptionHandler({MemberException.class})
    public ResponseEntity<staticErrorResponse> handleRestApiException(final MemberException exception) {
        log.warn("MemberException occur: ", exception);
        return this.makeErrorResponseEntity(exception.getErrorResult());
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
