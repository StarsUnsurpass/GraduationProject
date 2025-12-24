package com.graduationproject.power_fault_analysis.repository;

import com.graduationproject.power_fault_analysis.model.Component;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentRepository extends Neo4jRepository<Component, String> {
}
