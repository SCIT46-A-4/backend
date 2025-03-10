package com.scit.iLog.dto.analysis;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record ReAnalyzeRequestDTO(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime datetime,
        String weather, //stringified json data
        String locationName,
        double latitude,
        double longitude,
        String companion,
        String supplement
) {
}
