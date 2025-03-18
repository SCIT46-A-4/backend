package com.scit.iLog.controller;

import lombok.Getter;

@Getter
public enum SortOption {
    NAME("이름순"),
    BIRTH_DATE("생년월일순"),
    REGISTER_DATE("등록순");

    private final String displayName;

    SortOption(String displayName) {
        this.displayName = displayName;
    }
}
