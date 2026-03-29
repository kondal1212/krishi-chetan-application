package com.krishichetan.service.agents;

import com.krishichetan.dto.FinalRecommendation;
import com.krishichetan.dto.RagResult;
import com.krishichetan.dto.VisionResult;
import com.krishichetan.dto.WeatherResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RecommendationAgentService {

    public FinalRecommendation generate(
            VisionResult vision,
            RagResult rag,
            WeatherResult weather,
            String voiceText,
            String language
    ) {

        log.info("[RecommendationAgent] Generating final output");

        String problem = vision.disease() + " | Farmer says: " + voiceText;

        String weatherAdvice = weather.advisory();

        String translated = "తెలుగు: " + rag.solution(); // mock

        return new FinalRecommendation(
                problem,
                vision.confidence(),
                rag.solution(),
                rag.steps(),
                weatherAdvice,
                translated
        );
    }
}