package com.scit.iLog.dto.stats;

import java.util.List;

public record ChildPhysicalStatsDTO(
        List<ChildPhysicalStatPointDataDTO> seriesDataList
) {
}
