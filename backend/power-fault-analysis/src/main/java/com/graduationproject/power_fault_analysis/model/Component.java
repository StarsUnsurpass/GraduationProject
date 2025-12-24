package com.graduationproject.power_fault_analysis.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Node("Component")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Component {

    @Id
    @Property("name")
    private String name;

    @Relationship(type = "HAS_POSSIBLE_FAULT", direction = Relationship.Direction.OUTGOING)
    private List<FaultPhenomenon> possibleFaults;
}
