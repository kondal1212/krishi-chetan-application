package com.krishichetan.agent.repository;

import com.krishichetan.agent.model.AgentDecisionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AgentDecisionLog, Long> {
}
