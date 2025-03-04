package com.scit.iLog.dto.mentalsurvey.response;

import java.util.List;

public record SectionResponseDetailsDTO(
        String sectionTitle,
        List<QuestionResponseDetailsDTO> questionResponses,
        int sectionLikertScore
) {
}
