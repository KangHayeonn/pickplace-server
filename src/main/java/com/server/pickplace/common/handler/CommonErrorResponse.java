package com.server.pickplace.common.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommonErrorResponse {

    private final boolean success = false;
    private final int code;
    private final String message;

    public CommonErrorResponse(CommonErrorResult commonErrorResult) {

        this.code = commonErrorResult.getHttpStatus().value();
        this.message = commonErrorResult.getMessage();

    }
}
