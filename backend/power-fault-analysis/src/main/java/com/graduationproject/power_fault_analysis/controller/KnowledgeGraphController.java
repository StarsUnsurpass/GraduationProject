package com.graduationproject.power_fault_analysis.controller;

import com.graduationproject.power_fault_analysis.dto.GraphData;
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

    // --- Graph Data ---
    @GetMapping("/whole-graph")
    public ResponseEntity<GraphData> getWholeGraph() {
        return ResponseEntity.ok(knowledgeGraphService.getWholeGraph());
    }

    // --- DeviceType Endpoints ---
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

    @PostMapping("/devicetype/{deviceName}/component/{componentName}")
    public ResponseEntity<Void> addComponentToDeviceType(@PathVariable String deviceName, @PathVariable String componentName) {
        knowledgeGraphService.addComponentToDeviceType(deviceName, componentName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/devicetype/{deviceName}/component/{componentName}")
    public ResponseEntity<Void> removeComponentFromDeviceType(@PathVariable String deviceName, @PathVariable String componentName) {
        knowledgeGraphService.removeComponentFromDeviceType(deviceName, componentName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/devicetype/{name}")
    public ResponseEntity<Void> deleteDeviceType(@PathVariable String name) {
        knowledgeGraphService.deleteDeviceType(name);
        return ResponseEntity.ok().build();
    }

    // --- Component Endpoints ---
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

    @PostMapping("/component/{componentName}/fault/{faultName}")
    public ResponseEntity<Void> addFaultToComponent(@PathVariable String componentName, @PathVariable String faultName) {
        knowledgeGraphService.addFaultToComponent(componentName, faultName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/component/{componentName}/fault/{faultName}")
    public ResponseEntity<Void> removeFaultFromComponent(@PathVariable String componentName, @PathVariable String faultName) {
        knowledgeGraphService.removeFaultFromComponent(componentName, faultName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/component/{name}")
    public ResponseEntity<Void> deleteComponent(@PathVariable String name) {
        knowledgeGraphService.deleteComponent(name);
        return ResponseEntity.ok().build();
    }

    // --- FaultPhenomenon Endpoints ---
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

    @PostMapping("/faultphenomenon/{phenomenonName}/cause/{causeName}")
    public ResponseEntity<Void> addCauseToPhenomenon(@PathVariable String phenomenonName, @PathVariable String causeName) {
        knowledgeGraphService.addCauseToPhenomenon(phenomenonName, causeName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/faultphenomenon/{phenomenonName}/cause/{causeName}")
    public ResponseEntity<Void> removeCauseFromPhenomenon(@PathVariable String phenomenonName, @PathVariable String causeName) {
        knowledgeGraphService.removeCauseFromPhenomenon(phenomenonName, causeName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/faultphenomenon/{name}")
    public ResponseEntity<Void> deleteFaultPhenomenon(@PathVariable String name) {
        knowledgeGraphService.deleteFaultPhenomenon(name);
        return ResponseEntity.ok().build();
    }

    // --- FaultCause Endpoints ---
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

    @PostMapping("/faultcause/{causeName}/solution/{solutionName}")
    public ResponseEntity<Void> addSolutionToCause(@PathVariable String causeName, @PathVariable String solutionName) {
        knowledgeGraphService.addSolutionToCause(causeName, solutionName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/faultcause/{causeName}/solution/{solutionName}")
    public ResponseEntity<Void> removeSolutionFromCause(@PathVariable String causeName, @PathVariable String solutionName) {
        knowledgeGraphService.removeSolutionFromCause(causeName, solutionName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/faultcause/{name}")
    public ResponseEntity<Void> deleteFaultCause(@PathVariable String name) {
        knowledgeGraphService.deleteFaultCause(name);
        return ResponseEntity.ok().build();
    }

    // --- Solution Endpoints ---
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

    @DeleteMapping("/solution/{name}")
    public ResponseEntity<Void> deleteSolution(@PathVariable String name) {
        knowledgeGraphService.deleteSolution(name);
        return ResponseEntity.ok().build();
    }
}