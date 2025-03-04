package com.scit.iLog.dto.mentalsurvey.response;

public record QuestionResponseDetailsDTO(
        String questionItem,
        String answer,
        int score
) {
}
