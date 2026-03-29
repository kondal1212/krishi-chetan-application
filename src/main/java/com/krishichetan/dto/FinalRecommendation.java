package com.krishichetan.dto;

import java.util.List;

public record FinalRecommendation(
        String problem,
        double confidence,
        String organicSolution,
        List<String> steps,
        String weatherAdjustment,
        String languageOutput,
        List<String> agentTraceLogs
) {}