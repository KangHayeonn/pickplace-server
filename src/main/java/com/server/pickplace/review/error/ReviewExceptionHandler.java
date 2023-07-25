package com.server.pickplace.review.error;

import com.server.pickplace.common.dto.ErrorResponse;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.reservation.error.ReservationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ReviewExceptionHandler {

    @Autowired ResponseService responseService;

    @ExceptionHandler({ReviewException.class})
    public ResponseEntity<ErrorResponse> hostException(final ReviewException exception) {

        ErrorResponse errorResponse = getErrorResponse(exception);

        return ResponseEntity.ok(errorResponse);

    }
    private ErrorResponse getErrorResponse(ReviewException exception) {

        int code = exception.getErrorResult().getHttpStatus().value();
        String msg = exception.getErrorResult().getMessage();

        ErrorResponse errorResponse = responseService.getErrorResponse(code, msg);

        return errorResponse;

    }

}
