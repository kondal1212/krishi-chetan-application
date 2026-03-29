package com.krishichetan.service.agents;

import com.krishichetan.dto.RagResult;
import com.krishichetan.dto.VisionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class RagAgentService {

    public RagResult fetchContext(VisionResult vision) {
        log.info("[RagAgent] Fetching agricultural manual context for: {}", vision.disease());

        String disease = vision.disease().toLowerCase();

        if (disease.contains("powdery mildew")) {
            return new RagResult(
                    "Use neem oil (3ml/L) or a 10% milk-water spray solution.",
                    List.of("Prune heavily infected leaves", "Prepare neem solution", "Spray uniformly in the early morning"),
                    "ICAR Organic Farming Manual"
            );
        } else if (disease.contains("anthracnose")) {
            return new RagResult(
                    "Apply 1% Bordeaux mixture or Trichoderma viride.",
                    List.of("Remove fallen diseased leaves", "Improve canopy ventilation", "Apply copper-based organic fungicide"),
                    "National Horticulture Board"
            );
        }

        // Generic fallback for unrecognized diseases
        return new RagResult(
                "Ensure proper soil nutrition and moisture management.",
                List.of("Apply organic compost", "Check soil drainage", "Monitor daily"),
                "General Agricultural Best Practices"
        );
    }
}