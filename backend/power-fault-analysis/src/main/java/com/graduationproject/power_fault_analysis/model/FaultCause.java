package com.graduationproject.power_fault_analysis.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.ArrayList;

@Node
public class FaultCause {
    @Id
    private String name;

    private String description;

    @Relationship(type = "SOLVED_BY", direction = Relationship.Direction.OUTGOING)
    private List<Solution> solutions;

    public FaultCause() {}

    public FaultCause(String name, List<Solution> solutions) {
        this.name = name;
        this.solutions = solutions;
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

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solution> solutions) {
        this.solutions = solutions;
    }
}
