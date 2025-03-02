package com.scit.iLog.domain.child;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FamilyBackGround {
    DUAL_INCOME("맞벌이 가정"),
    SINGLE_INCOME_DAD("부 외벌이 가정"),
    SINGLE_INCOME_MOM("모 외벌이 가정"),
    SINGLE_PARENT("한부모 가정"),
    GRANDPARENTING("조손 가정"),
    MULTICULTURAL("다문화 가정"),
    SIBLINGS("형제자매"),
    ONLY_CHILD("외동"),
    SPECIAL_NEEDS("특수 아동"),
    COUNSELING_EXPERIENCE("상담센터 경험");

    private final String description;
}

