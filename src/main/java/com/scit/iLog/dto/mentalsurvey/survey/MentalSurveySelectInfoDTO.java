package com.scit.iLog.dto.mentalsurvey.survey;

import com.scit.iLog.domain.RelationType;

public record MentalSurveySelectInfoDTO(
        String title,
        String description,
        String link,
        RelationType type // 2025-03-13 / 김은진 추가
) {
}
