package com.graduationproject.power_fault_analysis.repository;

import com.graduationproject.power_fault_analysis.model.DeviceType;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceTypeRepository extends Neo4jRepository<DeviceType, String> {
}
