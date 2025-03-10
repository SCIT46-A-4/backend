package com.scit.iLog.controller;

import com.scit.iLog.dto.analysis.weather.WeatherResponse;
import com.scit.iLog.service.analysis.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping("/weather")
    public WeatherResponse handleGEtWeatherRecordRequest(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon,
            @RequestParam("dt") LocalDateTime dt
    ) {
        return weatherService.getWeatherRecordOf(lat, lon, dt);
    }
}
