package com.scit.iLog.dto.analysis;

import com.scit.iLog.domain.sentimentalAnalysis.EmotionType;
import lombok.Builder;

@Builder
public record ImageAnalysisResultDetailsDTO(
        String analysisResult,
        String suggestedSolution,
        double emotionScore,
        EmotionType emotionType,
        AnalysisResultNoteDetailsDTO analysisResultNote,
        AnalysisResultSatisfactionDetailsDTO analysisResultSatisfaction
) {
}
