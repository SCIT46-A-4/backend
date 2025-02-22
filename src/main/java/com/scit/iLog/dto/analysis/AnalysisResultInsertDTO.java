package com.scit.iLog.dto.analysis;

import com.scit.iLog.domain.sentimentalAnalysis.EmotionType;

public record AnalysisResultInsertDTO(
        String title,
        String analysisResult,
        String suggestedSolution,
        double emotionScore,
        EmotionType emotionType,
        AnalysisResultNoteInsertDTO analysisResultNote
) {
}
