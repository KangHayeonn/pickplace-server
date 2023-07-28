package com.server.pickplace.socialLogin.kakao.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.server.pickplace.common.service.ResponseService;
import com.server.pickplace.config.Helper;
import com.server.pickplace.member.dto.LoginResponseDto;
import com.server.pickplace.member.dto.TokenInfo;
import com.server.pickplace.member.entity.Member;
import com.server.pickplace.member.entity.MemberRole;
import com.server.pickplace.member.error.MemberErrorResult;
import com.server.pickplace.member.error.MemberException;
import com.server.pickplace.member.repository.MemberRepository;
import com.server.pickplace.member.repository.RefreshTokenRedisRepository;
import com.server.pickplace.member.service.jwt.JwtTokenProvider;
import com.server.pickplace.member.service.jwt.RefreshToken;
import com.server.pickplace.socialLogin.kakao.dto.SocialUserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.server.pickplace.member.entity.MemberRole.USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoUserService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.client_secret}")
    private String client_secret;

    public Map<String, Object> kakaoLogin(String code, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        String accessToken = getAccessToken(code); // 1. "인가 코드"로 "액세스 토큰" 요청
        SocialUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken); // 2. 토큰으로 카카오 API 호출
        Member kakaoUser = registerKakaoUserIfNeed(kakaoUserInfo); // 3. 카카오ID로 회원가입 처리
        return forceLogin(kakaoUser, request); //4. 강제 로그인 처리
    }

    public String getAccessToken (String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=17daada2a5511b9f5ad422950ad1c268"); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=http://localhost:3000/redirect"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

//            System.out.println("access_token : " + access_Token);
//            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }


//    private String getAccessToken(String code) throws JsonProcessingException {
//        // HTTP Header 생성
//        HttpHeaders headers = new HttpHeaders();
////        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        // HTTP Body 생성
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("grant_type", "authorization_code");
//        body.add("client_id", "17daada2a5511b9f5ad422950ad1c268");
//        body.add("redirect_uri", "http://localhost:3000/redirect");
//        body.add("client_secret", "VODb154UxG9n1uWAJ3UFvpEpfeT3vK3Y");
//        body.add("code", code);
//
//        // HTTP 요청 보내기
//        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
//
//
//
//        RestTemplate rt = new RestTemplate();
//
//        String response = rt.postForObject("https://kauth.kakao.com/oauth/token",kakaoTokenRequest, String.class);
//
//        assert response!=null ;
//
//        String responseBody = response.getBody();
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode jsonNode = objectMapper.readTree(responseBody);
//            return jsonNode.get("access_token").asText();
//
//
//
//
////        try{
////            ResponseEntity<String> response = rt.exchange(
////                    "https://kauth.kakao.com/oauth/token",
////                    HttpMethod.POST,
////                    kakaoTokenRequest,
////                    String.class
////            );
////
////            // HTTP 응답 (JSON) -> 액세스 토큰 파싱
////            String responseBody = response.getBody();
////            ObjectMapper objectMapper = new ObjectMapper();
////            JsonNode jsonNode = objectMapper.readTree(responseBody);
////            return jsonNode.get("access_token").asText();
////
////        }catch(Exception e){
////            throw new MemberException(MemberErrorResult.UNKNOWN_EXCEPTION);
////        }
//
//
//
//    }

    // 2. 토큰으로 카카오 API 호출
    private SocialUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // responseBody에 있는 정보를 꺼냄
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();

        return new SocialUserInfoDto(id, nickname, email);
    }

    // 3. 카카오ID로 회원가입 처리
    private Member registerKakaoUserIfNeed(SocialUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 email이 있는지 확인
        String kakaoEmail = kakaoUserInfo.getEmail();
        String nickname = kakaoUserInfo.getNickname();
        Member kakaoUser = memberRepository.findByEmail(kakaoEmail)
                .orElse(null);

        //|| !kakaoUser.getType().equals("kakao") 카카오 일때만 보는 로직 추가해야할듯
        if (kakaoUser == null ) {
            // 회원가입
            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            //새 맴버로 추가
            kakaoUser = memberRepository.save(Member.builder()
                    .name(nickname)
                    .email(kakaoEmail)
                    .role(USER)//USER용
                    .number("0")//default
                    .type("kakao")
                    .password("kakao")  // 저장 X
                    .build());

        }
        return kakaoUser;
    }

    private Map<String, Object> forceLogin(Member kakaoUser, HttpServletRequest request) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(kakaoUser.getEmail(), kakaoUser.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);


        refreshTokenRedisRepository.save(RefreshToken.builder()
                .id(authentication.getName())
                .ip(Helper.getClientIp(request))
                .authorities(authentication.getAuthorities())
                .refreshToken(tokenInfo.getRefreshToken())
                .build());

        Map<String, Object> loginMap = new HashMap<>();

        LoginResponseDto loginResponseDtoDto = LoginResponseDto.builder()
                .memberId(kakaoUser.getId())
                .nickname(kakaoUser.getName())
                .accessToken(tokenInfo.getAccessToken())
                .refreshToken(tokenInfo.getRefreshToken())
                .role("USER")
                .build();

        loginMap.put("member", loginResponseDtoDto);


        return loginMap;
    }
}