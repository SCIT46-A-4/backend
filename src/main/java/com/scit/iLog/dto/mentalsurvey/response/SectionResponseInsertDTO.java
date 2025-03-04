package com.scit.iLog.dto.mentalsurvey.response;

import java.util.List;

public record SectionResponseInsertDTO(
        String sectionTitle,
        List<QuestionResponseInsertDTO>questionResponses
) {
}
