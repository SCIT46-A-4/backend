package com.scit.iLog.dto.claims;

import java.time.LocalDateTime;
import java.util.List;

import com.scit.iLog.domain.claim.ClaimType;

import lombok.Builder;

@Builder
public record ClaimDetailsDTO(
        Long claimId,
//        String authorName,
        String title,
        String content,
        ClaimType type,
        LocalDateTime createdAt,
        List<ClaimAnswerDTO> claimAnswers
) {
}
