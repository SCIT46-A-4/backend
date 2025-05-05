package com.scit.iLog.domain.claim;

import lombok.Getter;

@Getter
public enum ClaimType {
    USAGE("利用方法"),
    PRIVACY("プライバシー"),
    GENERAL("その他");

    private final String typeNameKr;

    ClaimType(String typeNameKr) {
        this.typeNameKr = typeNameKr;
    }
}
