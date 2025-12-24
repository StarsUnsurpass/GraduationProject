package com.graduationproject.power_fault_analysis.repository;

import com.graduationproject.power_fault_analysis.model.Solution;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRepository extends Neo4jRepository<Solution, String> {
}
