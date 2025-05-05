package com.scit.iLog.domain.sentimentalAnalysis;

import lombok.Getter;

@Getter
public enum EmotionType {
    HAPPY("喜び"),
    SAD("悲しみ"),
    ANGRY("怒り"),
    FEAR("恐れ"),
    SURPRISED("驚き"),
    NEUTRAL("中立"),
    CONFUSED("混乱"),
    ANXIOUS("不安"),
    BORED("退屈"),
    EXCITED("興奮"),
    SHY("恥ずかしさ");

    private final String koreanName;

    EmotionType(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}

