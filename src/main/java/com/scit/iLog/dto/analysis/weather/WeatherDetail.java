package com.scit.iLog.dto.analysis.weather;

public record WeatherDetail(
        int id,
        String main,
        String description,
        String icon
) {}
