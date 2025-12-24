package com.graduationproject.power_fault_analysis.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Node("FaultPhenomenon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaultPhenomenon {

    @Id
    @Property("name")
    private String name;

    @Relationship(type = "CAUSED_BY", direction = Relationship.Direction.OUTGOING)
    private List<FaultCause> causes;
}
