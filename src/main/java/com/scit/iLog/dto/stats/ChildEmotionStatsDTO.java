package com.scit.iLog.dto.stats;

import java.util.List;

public record ChildEmotionStatsDTO(
        List<ChildEmotionStatPointDataDTO> seriesDataList
) {
}
