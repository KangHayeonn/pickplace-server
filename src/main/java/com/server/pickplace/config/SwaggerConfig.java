package com.server.pickplace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

import com.fasterxml.classmate.TypeResolver;
import com.server.pickplace.common.common.MyPageable;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * description    :
 * packageName    : config
 * fileName       : SwaggerConfig
 * author         : tkfdk
 * date           : 2023-05-28
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-05-28        tkfdk       최초 생성
 */


@Configuration
@EnableSwagger2
public class SwaggerConfig {
	TypeResolver typeResolver = new TypeResolver();

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
            .alternateTypeRules(AlternateTypeRules.newRule(typeResolver.resolve(Pageable.class),
                        typeResolver.resolve(MyPageable.class)))
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.server.pickplace"))
			.paths(PathSelectors.ant("/**"))
			.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("PickPlace Swagger")
			.description("원하는 공간을 실시간 추천/예약 서비스입니다.")
			.version("1.0.0")
			.build();
	}
}
