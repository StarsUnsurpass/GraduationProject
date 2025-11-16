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
 * 故障实体
 */
@Node("Fault")
@Data
public class Fault {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * 故障编码
     */
    @Property("faultCode")
    private String faultCode;

    /**
     * 故障名称
     */
    @Property("name")
    private String name;

    /**
     * 故障类型
     */
    @Property("type")
    private String type;

    /**
     * 故障分类（电气、机械、热力等）
     */
    @Property("category")
    private String category;

    /**
     * 严重程度（1-5）
     */
    @Property("severity")
    private Integer severity;

    /**
     * 影响范围
     */
    @Property("impact")
    private String impact;

    /**
     * 故障描述
     */
    @Property("description")
    private String description;

    /**
     * 故障对应的症状
     */
    @Relationship(type = "HAS_SYMPTOM", direction = Relationship.Direction.OUTGOING)
    private Set<Symptom> symptoms = new HashSet<>();

    /**
     * 故障原因
     */
    @Relationship(type = "CAUSED_BY", direction = Relationship.Direction.OUTGOING)
    private Set<Cause> causes = new HashSet<>();

    /**
     * 解决方案
     */
    @Relationship(type = "SOLVED_BY", direction = Relationship.Direction.OUTGOING)
    private Set<Solution> solutions = new HashSet<>();
}
