package com.graduationproject.power_fault_analysis.controller;

import com.graduationproject.power_fault_analysis.dto.GraphData;
import com.graduationproject.power_fault_analysis.service.KnowledgeGraphService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fault-analysis")
public class FaultAnalysisController {

    private final KnowledgeGraphService knowledgeGraphService;

    public FaultAnalysisController(KnowledgeGraphService knowledgeGraphService) {
        this.knowledgeGraphService = knowledgeGraphService;
    }

    @GetMapping("/diagnose")
    public ResponseEntity<GraphData> diagnose(@RequestParam String phenomenon) {
        return ResponseEntity.ok(knowledgeGraphService.getDiagnosis(phenomenon));
    }
}
