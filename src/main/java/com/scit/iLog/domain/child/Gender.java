package com.scit.iLog.domain.child;

import lombok.Getter;

@Getter
public enum Gender {
    MAN("男"),
    WOMAN("女"),
    NONE("なし");

    private final String typeNameKr;

    Gender(String typeNameKr) {
        this.typeNameKr = typeNameKr;
    }
}