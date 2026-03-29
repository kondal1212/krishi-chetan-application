package com.krishichetan.service.agents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishichetan.dto.FinalRecommendation;
import com.krishichetan.dto.RagResult;
import com.krishichetan.dto.VisionResult;
import com.krishichetan.dto.WeatherResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class RecommendationAgentService {
    private static final Logger log = LoggerFactory.getLogger(RecommendationAgentService.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    public RecommendationAgentService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public FinalRecommendation generate(VisionResult vision, RagResult rag, WeatherResult weather, String voiceText, String language) {
        String url = "[https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=](https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=)" + apiKey;

        // Structured prompt to force Gemini to return valid JSON
        String prompt = String.format(
                "Role: Agricultural Expert. Context: The crop has %s (Confidence: %.2f). " +
                        "Knowledge Base Solution: %s. Recommended Steps: %s. " +
                        "Current Weather: %s. Farmer's Query: %s. " +
                        "Task: Create a final recommendation. Return ONLY a JSON object with these keys: " +
                        "'solution', 'steps' (array of strings), 'weatherAdjustment', 'languageOutput' (translated to %s).",
                vision.disease(), vision.confidence(), rag.solution(), rag.steps(), weather.advisory(), voiceText, language
        );

        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(Map.of("text", prompt))
                    ))
            );

            String response = restTemplate.postForObject(url, requestBody, String.class);
            JsonNode root = objectMapper.readTree(response);

            // Navigate Gemini's response tree
            String rawJson = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

            // CRITICAL: Clean the JSON string from Markdown backticks
            String cleanJson = sanitizeJson(rawJson);
            JsonNode resNode = objectMapper.readTree(cleanJson);

            // Map to FinalRecommendation
            return new FinalRecommendation(
                    vision.disease(),
                    vision.confidence(),
                    resNode.path("solution").asText(rag.solution()),
                    objectMapper.convertValue(resNode.path("steps"), new TypeReference<List<String>>() {}),
                    resNode.path("weatherAdjustment").asText(weather.advisory()),
                    resNode.path("languageOutput").asText("No translation available"),
                    List.of()
            );

        } catch (Exception e) {
            log.error("[RecommendationAgent] Error generating AI recommendation: {}", e.getMessage());
            // Fallback to RAG/Weather data if LLM fails
            return new FinalRecommendation(
                    vision.disease(),
                    vision.confidence(),
                    rag.solution(),
                    rag.steps(),
                    weather.advisory(),
                    "క్షమించండి, AI స్పందన అందుబాటులో లేదు. (AI Error Fallback)",
                    List.of()
            );
        }
    }

    /**
     * Removes ```json and ``` markers that Gemini often includes.
     */
    private String sanitizeJson(String json) {
        return json.replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();
    }
}