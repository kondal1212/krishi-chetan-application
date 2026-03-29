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
        } else if (disease.contains("rice blast") || disease.contains("rice whitefly") || disease.contains("తెల్లదోమ")) {
            return new RagResult(
                    "Install yellow sticky traps and apply Neem oil (10000 ppm @ 3ml/L) or Beauveria bassiana.",
                    List.of(
                            "Drain water from the field for 3 to 4 days to reduce humidity at the base of the plants.",
                            "Install 10-15 yellow sticky traps per acre to attract and catch adult flies.",
                            "Spray neem oil solution directly targeting the base and lower stems of the paddy where pests hide."
                    ),
                    "Rice Knowledge Management Portal (RKMP) Best Practices"
            );
        } else if (disease.contains("grow") || disease.contains("నాటాలి")) {
            return new RagResult(
                    "Based on your black soil and Hyderabad's heat, we recommend Millet or Marigold.",
                    List.of("Prepare soil with 20% vermicompost", "Sow seeds at 2-inch depth", "Water every 3 days"),
                    "Beginner's Guide to Organic Farming"
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