package com.scit.iLog.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(Long memberId) {
        super(memberId.toString());
    }
}
