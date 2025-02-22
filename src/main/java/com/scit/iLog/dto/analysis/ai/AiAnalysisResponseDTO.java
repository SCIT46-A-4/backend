package com.scit.iLog.dto.analysis.ai;

import com.scit.iLog.domain.sentimentalAnalysis.EmotionType;
import lombok.Builder;

@Builder
public record AiAnalysisResponseDTO(
        String suggestedSolution,
        String analysisResult,
        double emotionScore,
        EmotionType emotionType
) {
}
