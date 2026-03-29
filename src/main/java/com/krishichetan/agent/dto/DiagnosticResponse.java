package com.krishichetan.agent.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiagnosticResponse {
    private String visualDiagnosis;
    private String climateContext;
    private String complianceStatus;
    private String localizedText;
    private String audioUrl; // The critical TTS requirement
}
