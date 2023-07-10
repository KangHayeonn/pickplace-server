package com.server.pickplace.healthCheck;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@AllArgsConstructor
public class TestController {
    @RequestMapping("/test")
    public String test(){
        System.out.println("@@@ doTest로 이동!");

        return  "test 변경";
    }

    @RequestMapping("/health")
    public ResponseEntity healthTest() {
        return new ResponseEntity(DefaultRes.res(StatusCode.OK,
                ResponseMessage.LOGIN_SUCCESS), HttpStatus.OK);
    }
}
