package com.krishichetan.agent.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class AgentDecisionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stepName; // e.g., "VISION_DIAGNOSIS"

    @Column(length = 2000)
    private String inputData;

    @Column(length = 2000)
    private String agentReasoning;

    private LocalDateTime timestamp = LocalDateTime.now();

    public AgentDecisionLog(String step, String input, String reasoning) {
        this.stepName = step;
        this.inputData = input;
        this.agentReasoning = reasoning;
    }

    public AgentDecisionLog() {
    }
}
