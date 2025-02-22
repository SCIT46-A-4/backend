package com.scit.iLog.dto.analysis;

import java.time.LocalDateTime;

public interface AnalysisResult {
    String getSavedFileUri();
    LocalDateTime getDate();
    String getWeather();
    String getCompanion();
    String getSupplementaryComment();
    String getAnalysisResult();
    String getSuggestedSolution();
    String getNote();
    int getSatisfactionScore();
}
