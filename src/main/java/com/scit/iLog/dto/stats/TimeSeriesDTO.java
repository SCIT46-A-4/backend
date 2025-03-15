package com.scit.iLog.dto.stats;

import java.time.Instant;

public record TimeSeriesDTO(
        Instant timestamp,  // UTC 타임스탬프
        double value,       // 측정값
        String link         // 클릭 시 이동할 URL
) {
}

