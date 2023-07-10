package com.server.pickplace.healthCheck;

import lombok.Data;

@Data
public class LoginReq {
    private String name;
    private String password;
}
