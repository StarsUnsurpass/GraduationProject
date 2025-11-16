package com.power.diagnosis.knowledge.domain;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * 症状实体
 */
@Node("Symptom")
@Data
public class Symptom {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * 症状编码
     */
    @Property("symptomCode")
    private String symptomCode;

    /**
     * 症状名称
     */
    @Property("name")
    private String name;

    /**
     * 症状类型
     */
    @Property("type")
    private String type;

    /**
     * 症状描述
     */
    @Property("description")
    private String description;

    /**
     * 检测方法
     */
    @Property("detectionMethod")
    private String detectionMethod;

    /**
     * 阈值
     */
    @Property("threshold")
    private String threshold;

    /**
     * 指示概率（0-1）
     */
    @Property("indicationProbability")
    private Double indicationProbability;
}
