package com.krishichetan.service.agents;

import com.krishichetan.dto.WeatherResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WeatherAgentService {

    public WeatherResult getCurrentWeather(String location) {
        log.info("[WeatherAgent] Fetching weather for {}", location);

        return new WeatherResult(
                32.0,
                85,
                "High humidity may accelerate fungal growth"
        );
    }
}
