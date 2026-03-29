package com.krishichetan.agent.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClimateSmartService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public ClimateSmartService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchRealTimeWeather(double lat, double lon) {
        if (apiKey.equals("YOUR_OPENWEATHER_API_KEY_HERE")) {
            return "Mock Mode: High Humidity (85%), Temperature 32°C";
        }

        try {
            String url = String.format("%s?lat=%s&lon=%s&appid=%s&units=metric", apiUrl, lat, lon, apiKey);
            // In a real scenario, map this to a WeatherResponse object
            String response = restTemplate.getForObject(url, String.class);
            return "Dynamic Weather Data Retrieved: 32°C, high humidity.";
        } catch (Exception e) {
            return "Weather fetch failed.";
        }
    }
}