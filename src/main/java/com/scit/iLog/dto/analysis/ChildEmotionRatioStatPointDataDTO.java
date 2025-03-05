package com.scit.iLog.dto.analysis;

import com.scit.iLog.domain.sentimentalAnalysis.EmotionType;

public record ChildEmotionRatioStatPointDataDTO(
        EmotionType emotionType,
        int emotionRatio,
        String label
) {
}
