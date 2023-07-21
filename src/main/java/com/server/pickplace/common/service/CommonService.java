package com.server.pickplace.common.service;

import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;

import static java.util.Base64.getUrlDecoder;

@Service
public class CommonService {

    public String getPayloadMapAndGetEmail(String accessToken) {

        String payloadJWT = accessToken.split("\\.")[1];
        Base64.Decoder decoder = getUrlDecoder();

        String payload = new String(decoder.decode(payloadJWT));
        JsonParser jsonParser = new BasicJsonParser();
        Map<String, Object> jsonArray = jsonParser.parseMap(payload);

        String email = (String) jsonArray.get("sub");

        return email;
    }

}
