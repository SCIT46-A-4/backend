package com.scit.iLog.service.analysis;

import com.scit.iLog.dto.analysis.weather.WeatherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// FeignClient 인터페이스
@FeignClient(name = "openWeatherClient", url = "https://api.openweathermap.org")
public interface OpenWeatherClient {
    //    appid=&units=metric&lang=kr
    @GetMapping("/data/3.0/onecall/timemachine")
    WeatherResponse getTimeMachineWeather(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon,
            @RequestParam("dt") long dt,
            @RequestParam("appid") String appid,
            @RequestParam(value = "units", defaultValue = "metric") String units,
            @RequestParam(value = "lang", defaultValue = "ja") String lang
    );
}
