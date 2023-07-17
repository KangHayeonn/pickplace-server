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

//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }

    private static final String[] AUTH_WHITELIST = {
            //정적인 파일에 대한 요청들 작성 (추후)
    };
    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http
                    .httpBasic().disable()
                    .csrf().disable() //postman test 위한 설정 변경
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
//                    .anyRequest().authenticated() //어떤 url이든 접근 인증 필요
                    .antMatchers("/api/v1/members/**","/send-mail/pwd").permitAll() // /members 관련 api 허락
                .and()
                    .formLogin()
//                    .loginPage("/view/login") //로그인 페이지 연결
                    .loginProcessingUrl("/login") //해당 주소를 어디로 처리할지 정해줌
                    .usernameParameter("id")
                    .passwordParameter("pw")
                    .defaultSuccessUrl("/view/dashboard", true) //성공시 이동할 url
                    .permitAll()
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

//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(loginIdPwValidator);
//    }


}