package com.krishichetan.service.agents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishichetan.dto.FinalRecommendation;
import com.krishichetan.dto.RagResult;
import com.krishichetan.dto.VisionResult;
import com.krishichetan.dto.WeatherResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RecommendationAgentService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    public RecommendationAgentService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public FinalRecommendation generate(VisionResult vision, RagResult rag, WeatherResult weather, String voiceText, String language) {
        log.info("[RecommendationAgent] Generating strict JSON recommendation in {}...", language);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=" + apiKey;

        // 1. Construct the Prompt
        String prompt = String.format(
                "Role: Agricultural Expert. Context: The crop has %s (Confidence: %.2f). " +
                        "Knowledge Base Solution: %s. Recommended Steps: %s. " +
                        "Current Weather: %s. Farmer's Query: %s. " +
                        "Task: Create a final recommendation. Return ONLY a JSON object with these exact keys: " +
                        "'problem', 'confidence' (float), 'organicSolution', 'steps' (array of strings), " +
                        "'weatherAdjustment', 'languageOutput' (translated to %s), 'agentTraceLogs' (empty array).",
                vision.disease(), vision.confidence(), rag.solution(), rag.steps(), weather.advisory(), voiceText, language
        );

        try {
            Map<String, Object> generationConfig = Map.of(
                    "response_mime_type", "application/json",
                    "temperature", 0.1  // Low temperature = more factual, less "creative"
            );

            // 2. Build the Request with generationConfig for guaranteed JSON
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                    "generationConfig", generationConfig
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 3. Execute the API Call
            String response = restTemplate.postForObject(url, requestEntity, String.class);
            JsonNode root = objectMapper.readTree(response);

            // 4. Extract the pure JSON string (No regex sanitization needed anymore!)
            String pureJson = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            JsonNode resNode = objectMapper.readTree(pureJson);

            // 5. Defensive Mapping (Using your excellent fallback strategy)
            return new FinalRecommendation(
                    resNode.path("problem").asText(vision.disease()),
                    resNode.path("confidence").asDouble(vision.confidence()),
                    resNode.path("organicSolution").asText(rag.solution()),
                    objectMapper.convertValue(resNode.path("steps"), new TypeReference<List<String>>() {}),
                    resNode.path("weatherAdjustment").asText(weather.advisory()),
                    resNode.path("languageOutput").asText("Translation pending..."),
                    List.of() // Trace logs will be populated by the Orchestrator
            );

        } catch (Exception e) {
            log.error("[RecommendationAgent] AI generation failed, triggering graceful degradation: {}", e.getMessage());

            // 6. Graceful Degradation (Returning safe data if the LLM is down)
            return new FinalRecommendation(
                    vision.disease(),
                    vision.confidence(),
                    rag.solution(),
                    rag.steps(), // Assuming RagResult.steps() returns List<String>
                    weather.advisory(),
                    "క్షమించండి, ప్రస్తుతం సిస్టమ్ బిజీగా ఉంది. దయచేసి మళ్లీ ప్రయత్నించండి. (System Error)",
                    List.of("Error: LLM Generation Failed. Using fallback context.")
            );
        }
    }
}