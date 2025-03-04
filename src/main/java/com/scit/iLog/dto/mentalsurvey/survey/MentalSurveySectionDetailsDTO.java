package com.scit.iLog.dto.mentalsurvey.survey;

import java.util.List;

public record MentalSurveySectionDetailsDTO(String sectionTitle, List<MentalSurveyQuestionDetailsDTO> questions) {
}
