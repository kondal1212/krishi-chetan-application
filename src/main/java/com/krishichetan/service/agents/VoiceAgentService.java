package com.krishichetan.service.agents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class VoiceAgentService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${sarvam.api.key}")
    private String apiKey;

    public VoiceAgentService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String transcribeAudio(MultipartFile audioFile) {
        log.info("[VoiceAgent] Sending audio to Sarvam AI (Saaras v3)...");

        if (audioFile == null || audioFile.isEmpty()) {
            return "No voice input provided.";
        }

        try {
            // Sarvam STT REST Endpoint
            String url = "https://api.sarvam.ai/speech-to-text";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("api-subscription-key", apiKey);

            // Wrap the audio bytes
            ByteArrayResource audioResource = new ByteArrayResource(audioFile.getBytes()) {
                @Override
                public String getFilename() {
                    return audioFile.getOriginalFilename() != null ? audioFile.getOriginalFilename() : "audio.wav";
                }
            };

            // Form data body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", audioResource);
            body.add("model", "saaras:v3");
            body.add("language_code", "te-IN"); // Explicitly setting to Telugu (India)
            body.add("mode", "transcribe");     // Other options: 'translate' or 'codemix'

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Execute the request
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            // Parse the response: Sarvam returns {"transcript": "...", "language_code": "..."}
            JsonNode root = objectMapper.readTree(response.getBody());
            String transcript = root.path("transcript").asText();

            log.info("[VoiceAgent] Transcription successful: {}", transcript);
            return transcript;

        } catch (Exception e) {
            log.error("[VoiceAgent] Sarvam AI failed: {}", e.getMessage());
            return "Fallback: Could not process Indian language audio.";
        }
    }
}