package com.scit.iLog.dto.mentalsurvey.survey;

import java.time.LocalDateTime;
import java.util.List;

public record MentalSurveyDetailsDTO(
        String id,
        String title,
        String description,
        List<MentalSurveySectionDetailsDTO>sections,
        LocalDateTime createdAt
) {
}
