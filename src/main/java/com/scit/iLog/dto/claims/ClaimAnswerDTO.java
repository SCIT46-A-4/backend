package com.scit.iLog.dto.claims;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ClaimAnswerDTO(
        Long answerId,
        String authorName,
//        String title,
        String content,
        LocalDateTime createdAt
) {
}
