package com.power.diagnosis.knowledge.domain;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * 故障原因实体
 */
@Node("Cause")
@Data
public class Cause {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * 原因编码
     */
    @Property("causeCode")
    private String causeCode;

    /**
     * 原因名称
     */
    @Property("name")
    private String name;

    /**
     * 原因类型
     */
    @Property("type")
    private String type;

    /**
     * 原因描述
     */
    @Property("description")
    private String description;

    /**
     * 根本原因/直接原因
     */
    @Property("causeLevel")
    private String causeLevel;

    /**
     * 发生频率
     */
    @Property("frequency")
    private String frequency;

    /**
     * 因果强度（0-1）
     */
    @Property("causalStrength")
    private Double causalStrength;
}
