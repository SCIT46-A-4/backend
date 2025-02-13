package com.scit.iLog.domain.child;

import lombok.Getter;

@Getter
public enum ChildAssetType {
    DRAWING("그림"),
    PHOTO("사진"),
    VOICE("음성"),
    WRITING("글");

    private final String typeNameKr;
    ChildAssetType(String typeNameKr) {
        this.typeNameKr = typeNameKr;
    }
}
