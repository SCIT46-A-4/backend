package com.scit.iLog.service;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String identifier) {
        super("해당 회원을 찾을 수 없습니다: " + identifier);
    }
}