package com.krishichetan.service.agents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishichetan.dto.WeatherResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherAgentService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${weather.api.key}")
    private String apiKey;

    public WeatherAgentService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public WeatherResult getCurrentWeather(String city) {
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric", city, apiKey);

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            double temp = root.path("main").path("temp").asDouble();
            int humidity = root.path("main").path("humidity").asInt();
            String condition = root.path("weather").get(0).path("description").asText();

            String advisory = (humidity > 80)
                    ? "High humidity (" + humidity + "%) detected. Fungal risk is high."
                    : "Weather is stable for treatment.";

            return new WeatherResult(temp, humidity, advisory + " Current: " + condition);

        } catch (Exception e) {
            return new WeatherResult(25.0, 50, "Weather service unavailable. Proceed with caution.");
        }
    }
}
