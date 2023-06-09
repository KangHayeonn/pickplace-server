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
public class SwaggerConfig {
	TypeResolver typeResolver = new TypeResolver();

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.OAS_30)
			.useDefaultResponseMessages(false)
			.select()
			.apis(RequestHandlerSelectors.basePackage("com.server.pickplace"))
			.paths(PathSelectors.any())
			.build()
			.apiInfo(apiInfo())
			.alternateTypeRules(AlternateTypeRules.newRule(typeResolver.resolve(Pageable.class),
				typeResolver.resolve(MyPageable.class)));
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("Pickplace Swagger")
			.description("Pickplace swagger config")
			.version("1.0")
			.build();
	}
}
