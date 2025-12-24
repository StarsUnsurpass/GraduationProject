package com.graduationproject.power_fault_analysis.repository;

import com.graduationproject.power_fault_analysis.model.FaultCause;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaultCauseRepository extends Neo4jRepository<FaultCause, String> {
}
