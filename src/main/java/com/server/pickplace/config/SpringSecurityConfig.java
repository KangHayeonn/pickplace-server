package com.server.pickplace.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.pickplace.common.dto.ErrorResponse;
import com.server.pickplace.member.service.jwt.JwtAuthenticationFilter;
import com.server.pickplace.member.service.jwt.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.io.PrintWriter;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * description    :
 * packageName    : config/security
 * fileName       : SpringSecurityConfig
 * author         : sohyun
 * date           : 2023-06-15
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-15        sohyun       최초 생성
 * 2023-06-26        sohyun       jwt 적용
 */

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    //Cor 해결
//    @Bean // 확인 ->
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3000);
    }

    private static final String[] AUTH_WHITELIST = {
            //정적인 파일에 대한 요청들 작성 (추후)
    };
    @Override
    protected void configure(HttpSecurity http) throws Exception {

            //이 부분은 정리 필요,,
            http
                    .httpBasic().disable();
            http
                    .exceptionHandling()
//                    .authenticationEntryPoint(unauthorizedEntryPoint) // 403 에러 예외처리
                    .and()
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/api/v1/review/places/*","/api/v1/review/*").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/v1/members/signup","/api/v1/members/login","/api/v1/members/emailCheck"
                            ,"/api/v1/search/**","/api/v1/members/kakaoLogin").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/v1/host/**").hasRole("HOST")
                    .antMatchers(HttpMethod.POST, "/api/v1/host/**").hasRole("HOST")
                    .anyRequest().authenticated() // 나머지는 403 에러 -> 에러 형식 200으로 보내야..
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    // JwtAuthenticationFilter 는 UsernamePasswordAuthenticationFilter 전에 넣음
                    .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/api/v1/members/logout")); //로그아웃조건 -> 다시 로그인 페이지로 자동 이동


 }

    //회원가입시 비밀번호 암호화에 사용할 인코더 빈 등록
    @Bean
    public BCryptPasswordEncoder encodePassword() {  // 회원가입 시 비밀번호 암호화에 사용할 Encoder 빈 등록
        return new BCryptPasswordEncoder();
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        //css 나 이미지 예외처리 (정적 요청 무시)
        web.ignoring()
                .antMatchers(AUTH_WHITELIST);    }


    //시큐리티에서 발생할 403에러 예외처리
//    private final AuthenticationEntryPoint unauthorizedEntryPoint =
//            (request, response, authException) -> {
//
//                ErrorResponse fail = new ErrorResponse(true, 403, "권한 인증 발생"); // Custom error response.
//                response.setStatus(
//                        HttpStatus.UNAUTHORIZED.value());
//                String json = objectMapper.writeValueAsString(fail);
//                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                PrintWriter writer = response.getWriter();
//                writer.write(json);
//                writer.flush();
//            };

}