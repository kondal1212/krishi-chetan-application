package com.krishichetan.service.agents;

import com.krishichetan.dto.RagResult;
import com.krishichetan.dto.VisionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class RagAgentService {

    public RagResult fetchContext(VisionResult vision) {
        log.info("[RagAgent] Fetching solution for {}", vision.disease());

        if (vision.disease().equalsIgnoreCase("Powdery Mildew")) {
            return new RagResult(
                    "Use neem oil (3ml/L) or milk spray",
                    List.of(
                            "Prune infected leaves",
                            "Prepare neem solution",
                            "Spray twice weekly"
                    ),
                    "ICAR Organic Farming Manual"
            );
        }

        return new RagResult(
                "Apply organic compost",
                List.of("Add compost", "Water properly"),
                "Fallback knowledge"
        );
    }
}