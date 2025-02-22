package com.scit.iLog.dto.analysis;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AnalysisResultListItemDTO(
        String analysisTargetFileSrcUri,
        String analysisResultTitle,
        LocalDateTime analysisDate
) {
}
