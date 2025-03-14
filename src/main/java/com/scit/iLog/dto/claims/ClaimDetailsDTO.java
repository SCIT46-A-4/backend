package com.scit.iLog.dto.claims;

import com.scit.iLog.domain.claim.ClaimType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ClaimDetailsDTO(
        Long claimId,
        String title,
        String content,
        ClaimType type,
        LocalDateTime createdAt,
        List<ClaimAnswerDTO> claimAnswers
) {
}
