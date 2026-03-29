package com.krishichetan.service.orchestrator;

import com.krishichetan.dto.FinalRecommendation;
import com.krishichetan.dto.RagResult;
import com.krishichetan.dto.VisionResult;
import com.krishichetan.dto.WeatherResult;
import com.krishichetan.service.agents.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AnalysisOrchestrator {

    private final VisionAgentService visionAgent;
    private final RagAgentService ragAgent;
    private final WeatherAgentService weatherAgent;
    private final RecommendationAgentService recommendationAgent;
    private final VoiceAgentService voiceAgent;

    public AnalysisOrchestrator(
            VisionAgentService visionAgent,
            RagAgentService ragAgent,
            WeatherAgentService weatherAgent,
            RecommendationAgentService recommendationAgent,
            VoiceAgentService voiceAgent
    ) {
        this.visionAgent = visionAgent;
        this.ragAgent = ragAgent;
        this.weatherAgent = weatherAgent;
        this.recommendationAgent = recommendationAgent;
        this.voiceAgent = voiceAgent;
    }

    public FinalRecommendation processImageRequest(
            MultipartFile image,
            MultipartFile audio,
            String location,
            String language
    ) {

        List<String> traceLogs = new ArrayList<>();

        try {
            // Voice
            String voiceText = voiceAgent.transcribeAudio(audio);
            traceLogs.add("Voice transcribed");

            // Vision
            VisionResult vision = visionAgent.analyzeImage(image.getBytes());
            traceLogs.add("Vision analysis completed: " + vision.disease());

            // RAG
            RagResult rag = ragAgent.fetchContext(vision);
            traceLogs.add("RAG retrieved organic solution");

            // Weather
            WeatherResult weather = weatherAgent.getCurrentWeather(location);
            traceLogs.add("Weather data fetched");

            // Final
            FinalRecommendation finalRec =
                    recommendationAgent.generate(vision, rag, weather, voiceText, language);

            traceLogs.add("Final recommendation generated");

            return new FinalRecommendation(
                    finalRec.problem(),
                    finalRec.confidence(),
                    finalRec.organicSolution(),
                    finalRec.steps(),
                    finalRec.weatherAdjustment(),
                    finalRec.languageOutput(),
                    traceLogs
            );

        } catch (Exception e) {
            log.error("Orchestration failed", e);
            throw new RuntimeException("Processing failed");
        }
    }
}
