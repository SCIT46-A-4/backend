package com.scit.iLog.dto.analysis;

import java.util.List;

public record ChildEmotionRatioDataDTO(
        List<ChildEmotionRatioStatPointDataDTO> seriesData
) {
}
