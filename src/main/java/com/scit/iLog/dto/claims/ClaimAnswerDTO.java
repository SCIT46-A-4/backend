package com.scit.iLog.dto.claims;

import lombok.Builder;

@Builder
public record ClaimAnswerDTO(
        Long answerId,
        String authorName,
        String title,
        String content
) {
}
