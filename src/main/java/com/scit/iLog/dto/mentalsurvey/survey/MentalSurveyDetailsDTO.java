package com.scit.iLog.dto.mentalsurvey.survey;

import com.scit.iLog.domain.RelationType;

import java.time.LocalDateTime;
import java.util.List;

public record MentalSurveyDetailsDTO(
        String id,
        String title,
        RelationType relationType,
        String description,
        List<MentalSurveySectionDetailsDTO>sections,
        LocalDateTime createdAt
) {
}
