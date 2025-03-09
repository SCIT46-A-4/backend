package com.scit.iLog.dto.mentalsurvey.response;

public record QuestionResponseInsertDTO(
        String questionItem,
        String example,
        int score
) {
}
