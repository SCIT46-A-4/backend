package com.scit.iLog.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(Long memberId) {
        super(memberId.toString());
    }

    public MemberNotFoundException(String reason) {
        super(String.format("회원 조회 실패: %s", reason));
    }
}
