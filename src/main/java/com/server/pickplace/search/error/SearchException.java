package com.server.pickplace.search.error;

import com.server.pickplace.host.error.HostErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchException extends RuntimeException {

    private final SearchErrorResult errorResult;

}
