package com.scit.iLog.dto.mentalsurvey;


import com.scit.iLog.dto.mentalsurvey.survey.MentalSurveyQuestionDTO;

import java.util.List;

public record MentalSurveySectionDTO(
        String title,
        List<MentalSurveyQuestionDTO>questions
) {
}
