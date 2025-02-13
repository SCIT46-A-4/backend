package com.scit.iLog.domain;

import lombok.Getter;

@Getter
public enum MemberRole {
    ADMIN("관리지"),
    USER("회원");

    private final String roleNameKr;

    MemberRole(String roleNameKr) {
        this.roleNameKr = roleNameKr;
    }

}
