package com.scit.iLog.domain;

import com.scit.iLog.domain.RelationType;
import lombok.Getter;

@Getter
public enum PermissionLevel {
    OWNER("소유자"),
    EDITOR("편집자"),
    VIEWER("뷰어");

    private final String typeNameKr;

    PermissionLevel(String typeNameKr) {
        this.typeNameKr = typeNameKr;
    }

    public static PermissionLevel getRelationTypeFromRelationType(RelationType relationType) {
        return switch (relationType) {
            case PARENT -> OWNER;
            case TEACHER -> VIEWER;
            default -> throw new IllegalStateException("존재하지 않는 관계입니다.: " + relationType);
        };
    }
}
