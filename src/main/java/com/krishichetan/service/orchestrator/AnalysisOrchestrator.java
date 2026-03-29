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
            VoiceAgentService voiceAgent) {
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
            String language) {

        List<String> traceLogs = new ArrayList<>();

        try {
            // 1. Voice
            String voiceText = voiceAgent.transcribeAudio(audio);
            traceLogs.add("Voice transcribed successfully");

            // 2. Vision
            VisionResult vision = visionAgent.analyzeImage(image.getBytes());
            traceLogs.add("Vision analysis completed: " + vision.disease());

            // 3. RAG
            RagResult rag = ragAgent.fetchContext(vision);
            traceLogs.add("RAG retrieved organic solution from: " + rag.source());

            // 4. Weather
            WeatherResult weather = weatherAgent.getCurrentWeather(location);
            traceLogs.add("Weather data fetched for: " + location);

            // 5. Final LLM Generation
            FinalRecommendation finalRec = recommendationAgent.generate(
                    vision, rag, weather, voiceText, language
            );
            traceLogs.add("Final recommendation generated via Gemini");

            // 6. Construct the final 7-argument record
            return new FinalRecommendation(
                    finalRec.problem(),
                    finalRec.confidence(),
                    finalRec.organicSolution(),
                    finalRec.steps(),
                    finalRec.weatherAdjustment(),
                    finalRec.languageOutput(),
                    traceLogs // Injecting the logs here
            );

        } catch (Exception e) {
            log.error("[Orchestrator] Pipeline failed", e);

            return new FinalRecommendation(
                    "System Error",
                    0.0,
                    "Unable to process request",
                    List.of("Retry", "Check input"),
                    "Check weather manually",
                    "సిస్టమ్ లోపం",
                    List.of("Failure in pipeline")
            );
        }
    }
}