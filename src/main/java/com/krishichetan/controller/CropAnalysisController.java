package com.krishichetan.controller;

import com.krishichetan.dto.FinalRecommendation;
import com.krishichetan.service.orchestrator.AnalysisOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/krishi")
@CrossOrigin(origins = "*") // Added for React frontend connectivity
public class CropAnalysisController {

    private static final Logger log = LoggerFactory.getLogger(CropAnalysisController.class);
    private final AnalysisOrchestrator orchestrator;

    public CropAnalysisController(AnalysisOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    /**
     * Unified multi-modal endpoint accepting both Image and Audio.
     */
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FinalRecommendation> analyzeCrop(
            @RequestParam("image") MultipartFile image,
            @RequestParam("audio") MultipartFile audio,
            @RequestParam(value = "location", defaultValue = "Hyderabad") String location,
            @RequestParam(value = "language", defaultValue = "Telugu") String language) {

        log.info("Received analysis request - Location: {}, Language: {}, Image Size: {} bytes, Audio Size: {} bytes",
                location, language, image.getSize(), audio.getSize());

        // Delegate the complex workflow to the Orchestrator
        FinalRecommendation response = orchestrator.processImageRequest(image, audio, location, language);

        return ResponseEntity.ok(response);
    }
}