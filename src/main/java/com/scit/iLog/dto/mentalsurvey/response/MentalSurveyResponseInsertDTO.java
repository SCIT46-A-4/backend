package com.scit.iLog.dto.mentalsurvey.response;

import java.util.List;

public record MentalSurveyResponseInsertDTO(
        String surveyTitle,
        List<SectionResponseInsertDTO> sectionResponses
) {
}
