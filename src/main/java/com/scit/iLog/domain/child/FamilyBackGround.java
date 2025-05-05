package com.scit.iLog.domain.child;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FamilyBackGround {
    DUAL_INCOME("共働き家庭"),
    SINGLE_INCOME_DAD("父のみ働く家庭"),
    SINGLE_INCOME_MOM("母のみ働く家庭"),
    SINGLE_PARENT("ひとり親家庭"),
    GRANDPARENTING("祖父母による育児家庭"),
    MULTICULTURAL("多文化家庭"),
    SIBLINGS("兄弟姉妹"),
    ONLY_CHILD("一人っ子"),
    SPECIAL_NEEDS("特別支援児"),
    COUNSELING_EXPERIENCE("カウンセリング経験");

    private final String description;
}

