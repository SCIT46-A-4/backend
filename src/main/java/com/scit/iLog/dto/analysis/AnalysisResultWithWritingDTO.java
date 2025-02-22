package com.scit.iLog.dto.analysis;

import com.scit.iLog.domain.sentimentalAnalysis.EmotionType;
import lombok.Builder;

@Builder
public record AnalysisResultWithWritingDTO(
        String analysisResult,
        String suggestedSolution,
        double emotionScore,
        EmotionType emotionType,
        AnalysisResultNoteDetailsDTO analysisResultNote,
        String analyzedText
) {
}
