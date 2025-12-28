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
}