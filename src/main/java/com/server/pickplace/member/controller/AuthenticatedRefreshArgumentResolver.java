//package com.server.pickplace.member.controller;
//
//import com.server.pickplace.member.error.MemberErrorResult;
//import com.server.pickplace.member.error.MemberException;
//import com.server.pickplace.member.service.jwt.JwtTokenProvider;
//import io.jsonwebtoken.Jwts;
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.MethodParameter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//
//import javax.servlet.http.HttpServletRequest;
//
//@Component
//@RequiredArgsConstructor
//public class AuthenticatedRefreshArgumentResolver implements HandlerMethodArgumentResolver {
//
//    private final JwtTokenProvider tokenProvider;
//
//    @Override
//    public boolean supportsParameter(final MethodParameter parameter) {
//        return parameter.hasParameterAnnotation(AuthenticatedRefresh.class);
//    }
//
//    @Override
//    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
//                                  final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
//        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
//        final String token = tokenProvider.resolveToken(request);
//
//        if (token == null) {
//            throw new MemberException(MemberErrorResult.UNKNOWN_EXCEPTION); // 예외 처리
//        }
//
//        Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//
//        return Long.valueOf(tokenProvider.getPayload(token));
//    }
//}
