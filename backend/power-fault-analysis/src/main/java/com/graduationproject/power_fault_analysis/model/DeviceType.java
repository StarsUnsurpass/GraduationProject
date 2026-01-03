package com.graduationproject.power_fault_analysis.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.ArrayList;

@Node
public class DeviceType {
    @Id
    private String name;
    
    private String description;

    @Relationship(type = "HAS_COMPONENT", direction = Relationship.Direction.OUTGOING)
    private List<Component> components;

    public DeviceType() {}
    
    public DeviceType(String name, List<Component> components) {
        this.name = name;
        this.components = components;
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

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }
}
