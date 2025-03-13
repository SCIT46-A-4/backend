package com.scit.iLog.domain.member;

import lombok.Getter;

@Getter
public enum MemberRole {
    ADMIN("관리자"),
    USER("회원"),
    LEAVED("탈퇴한 회원");

    private final String roleNameKr;

    MemberRole(String roleNameKr) {
        this.roleNameKr = roleNameKr;
    }
}
