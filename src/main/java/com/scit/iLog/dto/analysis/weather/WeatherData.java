package com.scit.iLog.dto.analysis.weather;

import java.util.List;

public record WeatherData(
        long dt,
        long sunrise,
        long sunset,
        double temp,
        double feels_like,
        int pressure,
        int humidity,
        double dew_point,
        double uvi,
        int clouds,
        int visibility,
        double wind_speed,
        int wind_deg,
        List<WeatherDetail> weather
) {
}

