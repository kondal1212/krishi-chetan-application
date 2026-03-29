package com.krishichetan.controller;

import com.krishichetan.dto.FinalRecommendation;
import com.krishichetan.service.orchestrator.AnalysisOrchestrator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/krishi")
public class CropAnalysisController {

    private final AnalysisOrchestrator orchestrator;

    public CropAnalysisController(AnalysisOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/analyze-image")
    public ResponseEntity<FinalRecommendation> analyzeCropImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("audio") MultipartFile audio,
            @RequestParam(value = "location", defaultValue = "Hyderabad") String location,
            @RequestParam(value = "language", defaultValue = "Telugu") String language) {

        FinalRecommendation response = orchestrator.processImageRequest(image, audio, location, language);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/voice-query")
    public ResponseEntity<String> processVoiceQuery(@RequestParam("audio") MultipartFile audio) {
        // Implementation for VoiceAgent -> Text -> Recommendation Orchestrator
        return ResponseEntity.ok("Voice processed successfully");
    }
}