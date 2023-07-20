package com.server.pickplace.host.error;

import com.server.pickplace.member.error.MemberErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HostException extends RuntimeException {
    private final HostErrorResult errorResult;

}
