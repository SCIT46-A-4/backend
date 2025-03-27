package com.scit.iLog.service.analysis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scit.iLog.dto.analysis.weather.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    private final OpenWeatherClient openWeatherClient;

    @Value("${openWeatherApiKey}")
    private String openWeatherApiKey;

    public WeatherResponse parseWeatherResponseFromJson(String weatherResponseJsonString) {
        try {
            return new ObjectMapper().readValue(weatherResponseJsonString, WeatherResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public WeatherResponse getWeatherRecordOf(double lat, double lon, LocalDateTime dt) {
        long unixTimestamp = dt.toEpochSecond(ZoneOffset.UTC);
        log.info(Long.toString(unixTimestamp));
        WeatherResponse weatherResponse = openWeatherClient.getTimeMachineWeather(lat, lon, unixTimestamp, openWeatherApiKey, "metric", "kr");
        log.info(weatherResponse.toString());
        return weatherResponse;
    }
}
