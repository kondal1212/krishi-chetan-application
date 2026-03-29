package com.krishichetan.dto;

public record VisionResult(
        String disease,
        double confidence,
        String description
) {}
