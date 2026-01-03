package com.graduationproject.power_fault_analysis.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.ArrayList;

@Node
public class FaultPhenomenon {
    @Id
    private String name;

    private String description;

    @Relationship(type = "CAUSED_BY", direction = Relationship.Direction.OUTGOING)
    private List<FaultCause> causes;

    public FaultPhenomenon() {}

    public FaultPhenomenon(String name, List<FaultCause> causes) {
        this.name = name;
        this.causes = causes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FaultCause> getCauses() {
        return causes;
    }

    public void setCauses(List<FaultCause> causes) {
        this.causes = causes;
    }
}
