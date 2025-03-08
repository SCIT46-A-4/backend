package com.scit.iLog.dto.analysis;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AnalysisResultListItemDTO(
        Long analysisTargetId,
        Long analysisResultId,
        String analysisTargetFileSrcUri,
        String analysisResultTitle,
        LocalDateTime analysisDate,
        LocalDateTime createdAt
) {
}
