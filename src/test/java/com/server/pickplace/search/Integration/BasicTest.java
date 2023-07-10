package com.server.pickplace.search.Integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class BasicTest {

    @Test
    void 환경변수꺼내기() throws Exception {
        String kakaoAK = System.getenv("KakaoAK");
        System.out.println("kakaoAK = " + kakaoAK);
    }


    @Test
    void api통신테스트() throws Exception {

        String position = URLEncoder.encode("서울특별시 동대문구 이문로 107");

        String stringURL = "https://dapi.kakao.com/v2/local/search/address?query=" + position;

        URL url = new URL(stringURL);
        String line;
        StringBuilder sb = new StringBuilder();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Authorization", "KakaoAK " + System.getenv("KakaoAK"));


// API 응답메시지를 불러와서 문자열로 저장
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
        }
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        String text = sb.toString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(text);

        JsonNode documentsNode = jsonNode.get("documents");
        if (documentsNode != null && documentsNode.isArray()) {
            JsonNode documentNode = documentsNode.get(0);
            if (documentNode != null) {
                Float xValue = (float) documentNode.get("x").asDouble();
                Float yValue = (float) documentNode.get("y").asDouble();

                System.out.println("x: " + xValue);
                System.out.println("y: " + yValue);

            }
        }
    }
}
