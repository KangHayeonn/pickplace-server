package com.server.pickplace.config;

import com.server.pickplace.member.service.jwt.JwtAuthenticationFilter;
import com.server.pickplace.member.service.jwt.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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

    private static final String[] AUTH_WHITELIST = {
            //정적인 파일에 대한 요청들 작성 (추후)
    };
    @Override
    protected void configure(HttpSecurity http) throws Exception {

            //이 부분은 정리 필요,,
            http
                    .httpBasic().disable();
            http.csrf().disable() //postman test 위한 설정 변경
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .anyRequest().permitAll()
//                    .antMatchers("/api/**").permitAll()
//                   .antMatchers("/api/v1/members/signup","/api/v1/members/login","/view/dashboard").permitAll() // /members 관련 api 허락
//                   .antMatchers("/api/v1/members/host").hasRole("HOST") // /members 관련 api 허락
//                     .antMatchers("/api/v1/members/user").hasRole("USER") // /members 관련 api 허락

//                    .anyRequest().authenticated() // 아닌건 오류
//                    .and()
////                    .formLogin()
////                    .loginPage("/view/login") //로그인 페이지 연결
////                    .loginProcessingUrl("/api/v1/members/login") //해당 주소를 어디로 처리할지 정해줌
//                    .usernameParameter("id")
//                    .passwordParameter("pw")
//                    .defaultSuccessUrl("/view/dashboard", true)
//                    .permitAll()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    // JwtAuthenticationFilter 는 UsernamePasswordAuthenticationFilter 전에 넣음
                    .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logoutProc")); //로그아웃조건 -> 다시 로그인 페이지로 자동 이동


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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://locakhost:3000/");
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}