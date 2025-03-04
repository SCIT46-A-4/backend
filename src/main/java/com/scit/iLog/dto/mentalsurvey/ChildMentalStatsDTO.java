package com.scit.iLog.dto.mentalsurvey;

import java.util.List;

public record ChildMentalStatsDTO(
        List<ChildMentalSurveyStatPointDataDTO> seriesData
) {
}
