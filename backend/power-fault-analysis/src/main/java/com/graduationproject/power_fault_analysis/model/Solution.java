package com.graduationproject.power_fault_analysis.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Node("Solution")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Solution {

    @Id
    @Property("name")
    private String name;

    // Additional properties can be added later
}
