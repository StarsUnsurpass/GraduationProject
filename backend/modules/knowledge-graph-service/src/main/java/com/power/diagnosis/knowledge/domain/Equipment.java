package com.power.diagnosis.knowledge.domain;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * 设备实体
 */
@Node("Equipment")
@Data
public class Equipment {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * 设备编号
     */
    @Property("equipmentCode")
    private String equipmentCode;

    /**
     * 设备名称
     */
    @Property("name")
    private String name;

    /**
     * 设备类型
     */
    @Property("type")
    private String type;

    /**
     * 设备型号
     */
    @Property("model")
    private String model;

    /**
     * 制造厂家
     */
    @Property("manufacturer")
    private String manufacturer;

    /**
     * 投运日期
     */
    @Property("commissionDate")
    private String commissionDate;

    /**
     * 额定电压（kV）
     */
    @Property("ratedVoltage")
    private Double ratedVoltage;

    /**
     * 额定容量
     */
    @Property("ratedCapacity")
    private Double ratedCapacity;

    /**
     * 安装位置
     */
    @Property("location")
    private String location;

    /**
     * 设备状态
     */
    @Property("status")
    private String status;

    /**
     * 描述
     */
    @Property("description")
    private String description;

    /**
     * 设备可能发生的故障
     */
    @Relationship(type = "HAS_FAULT", direction = Relationship.Direction.OUTGOING)
    private Set<Fault> faults = new HashSet<>();

    /**
     * 设备连接关系
     */
    @Relationship(type = "CONNECTS_TO", direction = Relationship.Direction.OUTGOING)
    private Set<Equipment> connectedEquipments = new HashSet<>();
}
