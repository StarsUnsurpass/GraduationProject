package com.graduationproject.power_fault_analysis.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Node
public class Component {
    @Id
    private String name;
    
    private String description;
    
    // stored as JSON or Key-Value text
    private String attributes;

    @Relationship(type = "HAS_POSSIBLE_FAULT", direction = Relationship.Direction.OUTGOING)
    private List<FaultPhenomenon> possibleFaults;

    public Component() {}

    public Component(String name, List<FaultPhenomenon> possibleFaults) {
        this.name = name;
        this.possibleFaults = possibleFaults;
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

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public List<FaultPhenomenon> getPossibleFaults() {
        return possibleFaults;
    }

    public void setPossibleFaults(List<FaultPhenomenon> possibleFaults) {
        this.possibleFaults = possibleFaults;
    }
}
