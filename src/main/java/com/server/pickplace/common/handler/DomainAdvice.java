package com.server.pickplace.common.handler;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Order(1)
@RestControllerAdvice
public @interface DomainAdvice {

}
