package com.scit.iLog.dto.mentalsurvey.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MentalSurveyResponseDetailsDTO(
        int totalLikertScore,
        List<SectionResponseDetailsDTO> sectionResponses,
        String comment,
        String surveyTitle,
        LocalDateTime createdAt,
        String responseId
) {
}
