package com.scit.iLog.domain.claim;

public enum ClaimType {
    USAGE("이용방법"),
    PRIVACY("개인정보보안"),
    GENERAL("기타");
    private final String typeNameKr;

    ClaimType(String typeNameKr) {
        this.typeNameKr = typeNameKr;
    }
}
