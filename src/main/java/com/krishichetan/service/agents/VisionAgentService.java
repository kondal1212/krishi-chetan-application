package com.krishichetan.service.agents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishichetan.dto.VisionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class VisionAgentService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    public VisionAgentService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public VisionResult analyzeImage(byte[] imageBytes) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        try {
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // Construct Gemini multimodal request body
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(
                                    Map.of("text", "Identify the crop disease in this image. Return ONLY a JSON object with keys 'disease' and 'confidence' (0-1)."),
                                    Map.of("inline_data", Map.of(
                                            "mime_type", "image/jpeg",
                                            "data", base64Image
                                    ))
                            )
                    ))
            );

            String response = restTemplate.postForObject(url, requestBody, String.class);
            JsonNode root = objectMapper.readTree(response);

            // Navigate Gemini's specific response structure
            String jsonText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

            // Clean up Markdown code blocks if Gemini includes them
            jsonText = jsonText.replace("```json", "").replace("```", "").trim();
            JsonNode resultNode = objectMapper.readTree(jsonText);

            return new VisionResult(
                    resultNode.path("disease").asText("Unknown Disease"),
                    resultNode.path("confidence").asDouble(0.0),
                    "Detected via Gemini Vision API"
            );

        } catch (Exception e) {
            log.error("[VisionAgent] Gemini API failed: {}", e.getMessage());
            return new VisionResult("Detection Failed", 0.0, "API Fallback active");
        }
    }
}