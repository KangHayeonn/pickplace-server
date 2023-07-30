package com.server.pickplace.reservation.error;

import com.server.pickplace.common.dto.ErrorResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.search.error.SearchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ReservationExceptionHandler {

    @Autowired ResponseService responseService;

    @ExceptionHandler({ReservationException.class})
    public ResponseEntity<ErrorResponse> hostException(final ReservationException exception) {

        ErrorResponse errorResponse = getErrorResponse(exception);

        return ResponseEntity.ok(errorResponse);

    }
    private ErrorResponse getErrorResponse(ReservationException exception) {

        int code = exception.getErrorResult().getHttpStatus().value();
        String msg = exception.getErrorResult().getMessage();

        ErrorResponse errorResponse = responseService.getErrorResponse(code, msg);

        return errorResponse;

    }

}
