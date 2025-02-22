package com.scit.iLog.dto.analysis;

import java.util.List;

public record AnalysisResultListDTO(
        List<AnalysisResultListItemDTO> analysisResults
) {
}
