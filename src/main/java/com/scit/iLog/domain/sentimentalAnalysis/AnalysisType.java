package com.scit.iLog.domain.sentimentalAnalysis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AnalysisType {
    WRITING("글"),
    DRAWING("그림"),
    PHOTO("사진"),
    VOICE("음성");

    private final String krName;
}
