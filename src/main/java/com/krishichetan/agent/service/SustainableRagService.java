package com.krishichetan.agent.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
public class SustainableRagService {

    @Data
    @AllArgsConstructor
    public static class RagContext {
        private String remedy;
        private String complianceStatus;
    }

    public RagContext processRemedyAndGuardrails(String diagnosis, String climateContext) {
        // 1. Simulate Vector DB Retrieval based on the dynamic diagnosis
        String rawRetrieval = "Standard agriculture practice for " + diagnosis + " involves chemical pesticides like Imidacloprid.";

        // 2. The Core Guardrail (PS #5 requirement)
        return applyComplianceGuardrail(rawRetrieval);
    }

    private RagContext applyComplianceGuardrail(String retrievedContext) {
        String lowerContext = retrievedContext.toLowerCase();

        // Block synthetic chemicals and force a Vedic/organic alternative
        if (lowerContext.contains("chemical") || lowerContext.contains("imidacloprid") || lowerContext.contains("pesticide")) {
            String safeRemedy = "Prepare a Neem-based spray (Dashparni Ark). Boil 5kg Neem leaves in 50L water. Spray at dawn to maximize absorption.";
            String complianceStatus = "BLOCKED: Synthetic chemicals flagged. Enforced organic Vedic remedy.";
            return new RagContext(safeRemedy, complianceStatus);
        }

        return new RagContext(retrievedContext, "COMPLIANT: Fully organic.");
    }
}