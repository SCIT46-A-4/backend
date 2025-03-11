package com.scit.iLog.domain;

import lombok.Getter;

@Getter
public enum RelationType {
    PARENT("부모"),
    TEACHER("교사"),
    CARER("보육자"),
    GUARDIAN("보호자");

    private final String typeNameKr;

    RelationType(String typeNameKr) {
        this.typeNameKr = typeNameKr;
    }
}
