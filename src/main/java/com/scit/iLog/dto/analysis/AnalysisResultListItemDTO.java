package com.scit.iLog.dto.analysis;

import com.scit.iLog.domain.sentimentalAnalysis.AnalysisType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AnalysisResultListItemDTO(
        Long analysisTargetId,
        Long analysisResultId,
        String analysisTargetFileSrcUri,
        String analysisResultTitle,
        List<AnalysisType> analysisTypes,
        LocalDateTime analysisDate,
        LocalDateTime createdAt
) {
}
