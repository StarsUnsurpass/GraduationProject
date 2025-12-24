package com.graduationproject.power_fault_analysis.controller;

import com.graduationproject.power_fault_analysis.model.*;
import com.graduationproject.power_fault_analysis.service.KnowledgeGraphService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge-graph")
public class KnowledgeGraphController {

    private final KnowledgeGraphService knowledgeGraphService;

    public KnowledgeGraphController(KnowledgeGraphService knowledgeGraphService) {
        this.knowledgeGraphService = knowledgeGraphService;
    }

    // DeviceType Endpoints
    @PostMapping("/devicetype")
    public ResponseEntity<DeviceType> createDeviceType(@RequestBody DeviceType deviceType) {
        return new ResponseEntity<>(knowledgeGraphService.saveDeviceType(deviceType), HttpStatus.CREATED);
    }

    @GetMapping("/devicetype/{name}")
    public ResponseEntity<DeviceType> getDeviceTypeByName(@PathVariable String name) {
        return knowledgeGraphService.findDeviceTypeByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/devicetype")
    public ResponseEntity<List<DeviceType>> getAllDeviceTypes() {
        return ResponseEntity.ok(knowledgeGraphService.findAllDeviceTypes());
    }

    // Component Endpoints
    @PostMapping("/component")
    public ResponseEntity<Component> createComponent(@RequestBody Component component) {
        return new ResponseEntity<>(knowledgeGraphService.saveComponent(component), HttpStatus.CREATED);
    }

    @GetMapping("/component/{name}")
    public ResponseEntity<Component> getComponentByName(@PathVariable String name) {
        return knowledgeGraphService.findComponentByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/component")
    public ResponseEntity<List<Component>> getAllComponents() {
        return ResponseEntity.ok(knowledgeGraphService.findAllComponents());
    }

    // FaultPhenomenon Endpoints
    @PostMapping("/faultphenomenon")
    public ResponseEntity<FaultPhenomenon> createFaultPhenomenon(@RequestBody FaultPhenomenon faultPhenomenon) {
        return new ResponseEntity<>(knowledgeGraphService.saveFaultPhenomenon(faultPhenomenon), HttpStatus.CREATED);
    }

    @GetMapping("/faultphenomenon/{name}")
    public ResponseEntity<FaultPhenomenon> getFaultPhenomenonByName(@PathVariable String name) {
        return knowledgeGraphService.findFaultPhenomenonByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/faultphenomenon")
    public ResponseEntity<List<FaultPhenomenon>> getAllFaultPhenomena() {
        return ResponseEntity.ok(knowledgeGraphService.findAllFaultPhenomena());
    }

    // FaultCause Endpoints
    @PostMapping("/faultcause")
    public ResponseEntity<FaultCause> createFaultCause(@RequestBody FaultCause faultCause) {
        return new ResponseEntity<>(knowledgeGraphService.saveFaultCause(faultCause), HttpStatus.CREATED);
    }

    @GetMapping("/faultcause/{name}")
    public ResponseEntity<FaultCause> getFaultCauseByName(@PathVariable String name) {
        return knowledgeGraphService.findFaultCauseByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/faultcause")
    public ResponseEntity<List<FaultCause>> getAllFaultCauses() {
        return ResponseEntity.ok(knowledgeGraphService.findAllFaultCauses());
    }

    // Solution Endpoints
    @PostMapping("/solution")
    public ResponseEntity<Solution> createSolution(@RequestBody Solution solution) {
        return new ResponseEntity<>(knowledgeGraphService.saveSolution(solution), HttpStatus.CREATED);
    }

    @GetMapping("/solution/{name}")
    public ResponseEntity<Solution> getSolutionByName(@PathVariable String name) {
        return knowledgeGraphService.findSolutionByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/solution")
    public ResponseEntity<List<Solution>> getAllSolutions() {
        return ResponseEntity.ok(knowledgeGraphService.findAllSolutions());
    }
}
