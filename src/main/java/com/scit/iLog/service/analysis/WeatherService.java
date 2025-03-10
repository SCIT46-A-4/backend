package com.scit.iLog.service.analysis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scit.iLog.dto.analysis.weather.WeatherResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    private static final String FAKE_WEATHER_RESPONSE = "{\"lat\":52.2297,\"lon\":21.0122,\"timezone\":\"Europe/Warsaw\",\"timezone_offset\":3600,\"data\":[{\"dt\":1645888976,\"sunrise\":1645853361,\"sunset\":1645891727,\"temp\":279.13,\"feels_like\":276.44,\"pressure\":1029,\"humidity\":64,\"dew_point\":272.88,\"uvi\":0.06,\"clouds\":0,\"visibility\":10000,\"wind_speed\":3.6,\"wind_deg\":340,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}]}]}";

    @Value("${openWeatherApiKey}")
    private String openWeatherApiKey;

    private final OpenWeatherClient openWeatherClient;

    public WeatherResponse parseWeatherResponseFromJson(String weatherResponseJsonString) {
        try {
            return new ObjectMapper().readValue(weatherResponseJsonString, WeatherResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public WeatherResponse getFakeWeatherResponse() {
        return parseWeatherResponseFromJson(FAKE_WEATHER_RESPONSE);
    }

    public WeatherResponse getWeatherRecordOf(double lat, double lon, LocalDateTime dt) {
        long unixTimestamp = dt.toEpochSecond(ZoneOffset.UTC);
        log.info(Long.toString(unixTimestamp));
        WeatherResponse weatherResponse = openWeatherClient.getTimeMachineWeather(lat, lon, unixTimestamp, openWeatherApiKey, "metric", "kr");
        log.info(weatherResponse.toString());
        return weatherResponse;
    }
}
