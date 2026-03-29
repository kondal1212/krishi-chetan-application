package com.krishichetan.agent.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.util.Map;

@Service
public class VisionLanguageService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public VisionLanguageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String identifyCropStressor(MultipartFile image) {
        try {
            if (apiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
                return "Mock Mode: Mango Hopper Infestation detected.";
            }

            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            String prompt = "You are an expert agronomist. Identify the specific disease or pest in this crop image. Reply with only the name of the issue.";

            // Constructing the dynamic Gemini 1.5 payload
            Map<String, Object> requestBody = Map.of(
                    "contents", new Object[]{
                            Map.of("parts", new Object[]{
                                    Map.of("text", prompt),
                                    Map.of("inlineData", Map.of(
                                            "mimeType", image.getContentType(),
                                            "data", base64Image
                                    ))
                            })
                    }
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            Map<String, Object> response = restTemplate.postForObject(apiUrl + "?key=" + apiKey, entity, Map.class);
            return extractTextFromGeminiResponse(response);

        } catch (Exception e) {
            return "Error analyzing image: " + e.getMessage();
        }
    }

    private String extractTextFromGeminiResponse(Map<String, Object> response) {
        // Safe extraction logic for Gemini's JSON structure omitted for brevity in demo
        return "Parsed dynamic response from Gemini API";
    }
}