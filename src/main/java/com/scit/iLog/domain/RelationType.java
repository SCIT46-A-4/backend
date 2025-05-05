package com.scit.iLog.domain;

import lombok.Getter;

@Getter
public enum RelationType {
    PARENT("부모"),
    TEACHER("先生"),
    CARER("보육자"),
    GUARDIAN("保護者"),
    ADMIN("관리자");

    private final String typeNameKr;

    RelationType(String typeNameKr) {
        this.typeNameKr = typeNameKr;
    }
}
