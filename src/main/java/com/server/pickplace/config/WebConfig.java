package com.server.pickplace.config;

import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedOrigins("http://localhost:3000")
                .allowedOrigins("https://pickplace.site")
                .allowedMethods(
                	HttpMethod.GET.name(),
                	HttpMethod.HEAD.name(),
                	HttpMethod.POST.name(),
                	HttpMethod.PUT.name(),
                	HttpMethod.DELETE.name())    
                .allowedHeaders("*")
                .maxAge(3000);
    }

}
