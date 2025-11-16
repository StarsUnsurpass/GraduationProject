package com.power.diagnosis.knowledge.repository;

import com.power.diagnosis.knowledge.domain.Fault;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 故障Repository
 */
@Repository
public interface FaultRepository extends Neo4jRepository<Fault, Long> {

    /**
     * 根据故障编码查询
     */
    Optional<Fault> findByFaultCode(String faultCode);

    /**
     * 根据故障名称模糊查询
     */
    List<Fault> findByNameContaining(String name);

    /**
     * 根据故障类型查询
     */
    List<Fault> findByType(String type);

    /**
     * 根据故障分类查询
     */
    List<Fault> findByCategory(String category);

    /**
     * 根据严重程度查询
     */
    List<Fault> findBySeverityGreaterThanEqual(Integer severity);

    /**
     * 查询故障及其所有症状
     */
    @Query("MATCH (f:Fault)-[:HAS_SYMPTOM]->(s:Symptom) WHERE f.faultCode = $faultCode RETURN f, collect(s)")
    Optional<Fault> findFaultWithSymptoms(@Param("faultCode") String faultCode);

    /**
     * 查询故障及其原因和解决方案
     */
    @Query("MATCH (f:Fault) WHERE f.faultCode = $faultCode " +
           "OPTIONAL MATCH (f)-[:CAUSED_BY]->(c:Cause) " +
           "OPTIONAL MATCH (f)-[:SOLVED_BY]->(s:Solution) " +
           "RETURN f, collect(DISTINCT c) as causes, collect(DISTINCT s) as solutions")
    Optional<Fault> findFaultWithCausesAndSolutions(@Param("faultCode") String faultCode);

    /**
     * 根据症状查找可能的故障
     */
    @Query("MATCH (s:Symptom)<-[:HAS_SYMPTOM]-(f:Fault) WHERE s.symptomCode IN $symptomCodes " +
           "RETURN f, count(s) as matchCount ORDER BY matchCount DESC")
    List<Fault> findFaultsBySymptoms(@Param("symptomCodes") List<String> symptomCodes);
}
