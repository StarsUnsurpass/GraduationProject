package com.graduationproject.power_fault_analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphLink {
    private String source;
    private String target;
    private String name;
}
