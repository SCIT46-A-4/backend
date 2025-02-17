package com.scit.iLog.domain.sentimentalAnalysis;

import lombok.Getter;

@Getter
public enum EmotionType {
    HAPPY("기쁨"),
    SAD("슬픔"),
    ANGRY("분노"),
    FEAR("두려움"),
    SURPRISED("놀람"),
    NEUTRAL("중립"),
    CONFUSED("혼란"),
    ANXIOUS("불안"),
    BORED("지루함"),
    EXCITED("흥분"),
    SHY("수줍음");

    private final String koreanName;

    EmotionType(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}

