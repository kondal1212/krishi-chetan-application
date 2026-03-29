package com.krishichetan.service.agents;

import com.krishichetan.dto.VisionResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VisionAgentService {

    public VisionResult analyzeImage(byte[] imageBytes) {
        log.info("[VisionAgent] Analyzing image...");

        try {
            // MOCK (replace with Gemini later)
            return new VisionResult(
                    "Powdery Mildew",
                    0.88,
                    "White powdery spots observed on leaves"
            );

        } catch (Exception e) {
            log.error("Vision failed", e);
            return new VisionResult(
                    "Unknown",
                    0.5,
                    "Fallback analysis"
            );
        }
    }
}
