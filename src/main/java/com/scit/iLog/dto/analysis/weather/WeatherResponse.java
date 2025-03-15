package com.scit.iLog.dto.analysis.weather;

import java.util.List;

public record WeatherResponse(
        double lat,
        double lon,
        String timezone,
        int timezone_offset,
        List<WeatherData> data
) {
}

