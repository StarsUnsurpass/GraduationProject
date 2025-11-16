package com.power.diagnosis.knowledge.repository;

import com.power.diagnosis.knowledge.domain.Equipment;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 设备Repository
 */
@Repository
public interface EquipmentRepository extends Neo4jRepository<Equipment, Long> {

    /**
     * 根据设备编号查询
     */
    Optional<Equipment> findByEquipmentCode(String equipmentCode);

    /**
     * 根据设备名称模糊查询
     */
    List<Equipment> findByNameContaining(String name);

    /**
     * 根据设备类型查询
     */
    List<Equipment> findByType(String type);

    /**
     * 根据设备状态查询
     */
    List<Equipment> findByStatus(String status);

    /**
     * 查询设备及其所有故障
     */
    @Query("MATCH (e:Equipment)-[:HAS_FAULT]->(f:Fault) WHERE e.equipmentCode = $equipmentCode RETURN e, collect(f)")
    Optional<Equipment> findEquipmentWithFaults(@Param("equipmentCode") String equipmentCode);

    /**
     * 查询设备及其连接的所有设备
     */
    @Query("MATCH (e:Equipment)-[:CONNECTS_TO*1..2]-(connected:Equipment) WHERE e.equipmentCode = $equipmentCode RETURN e, collect(connected)")
    Optional<Equipment> findEquipmentWithConnections(@Param("equipmentCode") String equipmentCode);

    /**
     * 统计各类型设备数量
     */
    @Query("MATCH (e:Equipment) RETURN e.type as type, count(e) as count")
    List<Object> countByType();
}
