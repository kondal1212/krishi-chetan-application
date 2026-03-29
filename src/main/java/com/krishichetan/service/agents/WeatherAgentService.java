package com.krishichetan.service.agents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishichetan.dto.WeatherResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j // 1. Added Lombok logger
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
        log.info("[WeatherAgent] Fetching current weather for location: {}", city);

        // 2. Safe URL Construction: Automatically handles spaces and special characters
        String url = UriComponentsBuilder.fromHttpUrl("https://api.openweathermap.org/data/2.5/weather")
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();

        try {
            log.debug("[WeatherAgent] Calling OpenWeatherMap API...");
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            double temp = root.path("main").path("temp").asDouble();
            int humidity = root.path("main").path("humidity").asInt();
            String condition = root.path("weather").get(0).path("description").asText();

            log.info("[WeatherAgent] Success: {}°C, {}% humidity, Condition: {}", temp, humidity, condition);

            String advisory = (humidity > 80)
                    ? "High humidity (" + humidity + "%) detected. Fungal risk is high. Avoid spraying if rain is expected."
                    : "Weather is stable for treatment.";

            return new WeatherResult(temp, humidity, advisory + " Current conditions: " + condition);

        } catch (HttpClientErrorException e) {
            // 3. Specific catching for 404 (City Not Found) or 401 (Invalid API Key)
            log.error("[WeatherAgent] Client error for city '{}': HTTP {} - {}", city, e.getStatusCode(), e.getMessage());
            return getFallbackWeather();
        } catch (Exception e) {
            // 4. Catch-all for network timeouts or JSON parsing errors
            log.error("[WeatherAgent] Unexpected error fetching weather for '{}': {}", city, e.getMessage());
            return getFallbackWeather();
        }
    }

    // Extracted fallback logic to keep the main method clean
    private WeatherResult getFallbackWeather() {
        log.warn("[WeatherAgent] Returning fallback weather data.");
        return new WeatherResult(
                25.0,
                50,
                "Weather service temporarily unavailable. Please assess local conditions before proceeding."
        );
    }
}