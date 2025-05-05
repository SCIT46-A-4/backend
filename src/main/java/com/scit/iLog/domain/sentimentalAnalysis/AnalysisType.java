package com.scit.iLog.domain.sentimentalAnalysis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AnalysisType {
    WRITING("文"),
    DRAWING("絵"),
    PHOTO("写真");

    private final String krName;
}
