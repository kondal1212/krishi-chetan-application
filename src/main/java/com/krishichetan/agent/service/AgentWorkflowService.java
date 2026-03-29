package com.krishichetan.agent.service;

import com.krishichetan.agent.dto.DiagnosticResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AgentWorkflowService {

    private final VisionLanguageService visionService;
    private final ClimateSmartService climateService;
    private final SustainableRagService ragService;
    private final LocalizationService localizationService;

    public DiagnosticResponse executePipeline(MultipartFile image, double lat, double lon, String language) {

        String visualDiagnosis = visionService.identifyCropStressor(image);
        String climateContext = climateService.fetchRealTimeWeather(lat, lon);

        SustainableRagService.RagContext ragContext = ragService.processRemedyAndGuardrails(visualDiagnosis, climateContext);

        String localizedText = localizationService.translateText(ragContext.getRemedy(), language);
        String audioUrl = localizationService.generateAudioUrl(localizedText, language);

        return DiagnosticResponse.builder()
                .visualDiagnosis(visualDiagnosis)
                .climateContext(climateContext)
                .complianceStatus(ragContext.getComplianceStatus())
                .localizedText(localizedText)
                .audioUrl(audioUrl)
                .build();
    }
}