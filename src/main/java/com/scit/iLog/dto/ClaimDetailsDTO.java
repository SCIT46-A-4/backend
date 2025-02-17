package com.scit.iLog.dto;

import com.scit.iLog.domain.claim.ClaimType;
import com.scit.iLog.dto.claims.ClaimAnswerDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record ClaimDetailsDTO(
        Long claimId,
        String authorName,
        String title,
        String content,
        ClaimType type,
        List<ClaimAnswerDTO> claimAnswers
) {
}
