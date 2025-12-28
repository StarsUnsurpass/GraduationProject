package com.graduationproject.power_fault_analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphData {
    private List<GraphNode> nodes;
    private List<GraphLink> links;
}
