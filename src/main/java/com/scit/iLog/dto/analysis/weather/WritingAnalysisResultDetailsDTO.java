package com.scit.iLog.dto.analysis.weather;

import com.scit.iLog.domain.sentimentalAnalysis.EmotionType;
import com.scit.iLog.dto.analysis.AnalysisResultNoteDetailsDTO;

public record WritingAnalysisResultDetailsDTO(
        String analysisResult,
        String suggestedSolution,
        double emotionScore,
        EmotionType emotionType,
        AnalysisResultNoteDetailsDTO analysisResultNote,
        String analyzedText
) {
}
