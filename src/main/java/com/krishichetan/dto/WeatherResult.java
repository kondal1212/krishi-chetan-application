package com.krishichetan.dto;

public record WeatherResult(
        double temperature,
        int humidity,
        String advisory
) {}
