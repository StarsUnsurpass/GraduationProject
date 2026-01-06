package com.graduationproject.power_fault_analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphNode {
    private String id;
    private String name;
    private String category;
    private String description;
    private String attributes;
    
    // Constructor for backward compatibility if needed, or just update callers
    public GraphNode(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }
}