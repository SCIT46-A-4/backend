package com.scit.iLog.dto.stats;

import java.util.List;

public record ChildMentalStatsDTO(
        List<ChildMentalSurveyStatPointDataDTO> seriesDataList
) {
}
