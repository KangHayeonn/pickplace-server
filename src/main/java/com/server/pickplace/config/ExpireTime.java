package com.server.pickplace.config;

public class ExpireTime {

    public static final long ACCESS_TOKEN_EXPIRE_TIME = 100 * 10 * 60 * 1000L;               //10분
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;     //7일
    public static final long REFRESH_TOKEN_EXPIRE_TIME_FOR_REDIS = REFRESH_TOKEN_EXPIRE_TIME / 1000L;
}
