package com.krishichetan.repository;

import com.krishichetan.model.AgentDecisionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AgentDecisionLog, Long> {
}
