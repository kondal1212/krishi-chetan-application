package com.krishichetan.agent.controller;

import com.krishichetan.agent.dto.DiagnosticResponse;
import com.krishichetan.agent.service.AgentWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/diagnostic")
@RequiredArgsConstructor
public class DiagnosticController {

    private final AgentWorkflowService workflowService;

    @PostMapping("/analyze")
    public ResponseEntity<DiagnosticResponse> analyzeCrop(
            @RequestParam("image") MultipartFile image,
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("language") String targetLanguage) {

        DiagnosticResponse response = workflowService.executePipeline(image, latitude, longitude, targetLanguage);
        return ResponseEntity.ok(response);
    }
}