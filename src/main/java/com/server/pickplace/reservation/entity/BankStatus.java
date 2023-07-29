package com.server.pickplace.reservation.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BankStatus {

    KUKMIN("국민은행", "1020315-12108542"), // 대기중

    HANA("하나은행", "2020315-12108542"), // 인증됨 : 결제 및 예약 대기

    SHINHAN("신한은행", "3020315-12108542"),

    URI("우리은행", "4020315-12108542");

    // 추후 추가 필요

    private final String bankName;
    private final String bankNum;


}
