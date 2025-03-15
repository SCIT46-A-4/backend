package com.scit.iLog.dto.claims;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ClaimAnswerDTO(
        Long answerId,
        String authorName,
        String content,
        LocalDateTime createdAt
) {
}
