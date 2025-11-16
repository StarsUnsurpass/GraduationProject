package com.power.diagnosis.knowledge.domain;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * 解决方案实体
 */
@Node("Solution")
@Data
public class Solution {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * 方案编码
     */
    @Property("solutionCode")
    private String solutionCode;

    /**
     * 方案名称
     */
    @Property("name")
    private String name;

    /**
     * 方案类型
     */
    @Property("type")
    private String type;

    /**
     * 方案描述
     */
    @Property("description")
    private String description;

    /**
     * 操作步骤
     */
    @Property("steps")
    private String steps;

    /**
     * 所需工具
     */
    @Property("requiredTools")
    private String requiredTools;

    /**
     * 预计时间（小时）
     */
    @Property("estimatedTime")
    private Double estimatedTime;

    /**
     * 预计成本
     */
    @Property("estimatedCost")
    private Double estimatedCost;

    /**
     * 有效性评分（0-10）
     */
    @Property("effectivenessScore")
    private Double effectivenessScore;

    /**
     * 应用次数
     */
    @Property("applicationCount")
    private Integer applicationCount;

    /**
     * 注意事项
     */
    @Property("precautions")
    private String precautions;
}
