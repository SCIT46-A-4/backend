package com.scit.iLog.domain.child;

import lombok.Getter;

@Getter
public enum Gender {
    MAN("남"),
	WOMAN("여"),
	NONE("선택안함");

	private final String typeNameKr;

    Gender(String typeNameKr) {
        this.typeNameKr = typeNameKr;
    }
}