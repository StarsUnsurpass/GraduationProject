package com.graduationproject.power_fault_analysis.service;

import com.graduationproject.power_fault_analysis.dto.GraphData;
import com.graduationproject.power_fault_analysis.dto.GraphLink;
import com.graduationproject.power_fault_analysis.dto.GraphNode;
import com.graduationproject.power_fault_analysis.model.*;
import com.graduationproject.power_fault_analysis.repository.*;
import org.neo4j.driver.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class KnowledgeGraphService {

    private final DeviceTypeRepository deviceTypeRepository;
    private final ComponentRepository componentRepository;
    private final FaultPhenomenonRepository faultPhenomenonRepository;
    private final FaultCauseRepository faultCauseRepository;
    private final SolutionRepository solutionRepository;
    private final Neo4jClient neo4jClient;

    public KnowledgeGraphService(DeviceTypeRepository deviceTypeRepository,
                                 ComponentRepository componentRepository,
                                 FaultPhenomenonRepository faultPhenomenonRepository,
                                 FaultCauseRepository faultCauseRepository,
                                 SolutionRepository solutionRepository,
                                 Neo4jClient neo4jClient) {
        this.deviceTypeRepository = deviceTypeRepository;
        this.componentRepository = componentRepository;
        this.faultPhenomenonRepository = faultPhenomenonRepository;
        this.faultCauseRepository = faultCauseRepository;
        this.solutionRepository = solutionRepository;
        this.neo4jClient = neo4jClient;
    }

    // --- Basic CRUD ---

    // DeviceType
    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public DeviceType saveDeviceType(DeviceType deviceType) {
        return deviceTypeRepository.save(deviceType);
    }

    public Optional<DeviceType> findDeviceTypeByName(String name) {
        return deviceTypeRepository.findById(name);
    }

    public List<DeviceType> findAllDeviceTypes() {
        return deviceTypeRepository.findAll();
    }

    // Component
    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public Component saveComponent(Component component) {
        return componentRepository.save(component);
    }

    public Optional<Component> findComponentByName(String name) {
        return componentRepository.findById(name);
    }

    public List<Component> findAllComponents() {
        return componentRepository.findAll();
    }

    // FaultPhenomenon
    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public FaultPhenomenon saveFaultPhenomenon(FaultPhenomenon faultPhenomenon) {
        return faultPhenomenonRepository.save(faultPhenomenon);
    }

    public Optional<FaultPhenomenon> findFaultPhenomenonByName(String name) {
        return faultPhenomenonRepository.findById(name);
    }

    public List<FaultPhenomenon> findAllFaultPhenomena() {
        return faultPhenomenonRepository.findAll();
    }

    // FaultCause
    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public FaultCause saveFaultCause(FaultCause faultCause) {
        return faultCauseRepository.save(faultCause);
    }

    public Optional<FaultCause> findFaultCauseByName(String name) {
        return faultCauseRepository.findById(name);
    }

    public List<FaultCause> findAllFaultCauses() {
        return faultCauseRepository.findAll();
    }

    // Solution
    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public Solution saveSolution(Solution solution) {
        return solutionRepository.save(solution);
    }

    public Optional<Solution> findSolutionByName(String name) {
        return solutionRepository.findById(name);
    }

    public List<Solution> findAllSolutions() {
        return solutionRepository.findAll();
    }

    // --- Rename Operation ---
    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public void renameEntity(String label, String oldName, String newName) {
        if (!Set.of("DeviceType", "Component", "FaultPhenomenon", "FaultCause", "Solution").contains(label)) {
            throw new IllegalArgumentException("Invalid entity label: " + label);
        }
        String checkQuery = String.format("MATCH (n:%s {name: $newName}) RETURN count(n)", label);
        Long count = neo4jClient.query(checkQuery)
                .bind(newName).to("newName")
                .fetchAs(Long.class)
                .one()
                .orElse(0L);
        if (count > 0) {
            throw new IllegalArgumentException("Entity with name '" + newName + "' already exists.");
        }
        String query = String.format("MATCH (n:%s {name: $oldName}) SET n.name = $newName", label);
        neo4jClient.query(query)
                .bind(oldName).to("oldName")
                .bind(newName).to("newName")
                .run();
    }

    // --- Delete Operations ---

    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public void deleteDeviceType(String name) {
        deviceTypeRepository.deleteById(name);
    }

    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public void deleteComponent(String name) {
        componentRepository.deleteById(name);
    }

    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public void deleteFaultPhenomenon(String name) {
        faultPhenomenonRepository.deleteById(name);
    }

    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public void deleteFaultCause(String name) {
        faultCauseRepository.deleteById(name);
    }

    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public void deleteSolution(String name) {
        solutionRepository.deleteById(name);
    }

    // --- Relationship Management (Add & Remove) ---

    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public void addComponentToDeviceType(String deviceName, String componentName) {
        DeviceType device = deviceTypeRepository.findById(deviceName).orElseThrow(() -> new RuntimeException("Device not found"));
        Component component = componentRepository.findById(componentName).orElseThrow(() -> new RuntimeException("Component not found"));

        if (device.getComponents() == null) device.setComponents(new ArrayList<>());
        device.getComponents().add(component);
        deviceTypeRepository.save(device);
    }

    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public void removeComponentFromDeviceType(String deviceName, String componentName) {
        DeviceType device = deviceTypeRepository.findById(deviceName).orElseThrow(() -> new RuntimeException("Device not found"));
        if (device.getComponents() != null) {
            device.getComponents().removeIf(c -> c.getName().equals(componentName));
            deviceTypeRepository.save(device);
        }
    }

    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public void addFaultToComponent(String componentName, String faultName) {
        Component component = componentRepository.findById(componentName).orElseThrow(() -> new RuntimeException("Component not found"));
        FaultPhenomenon fault = faultPhenomenonRepository.findById(faultName).orElseThrow(() -> new RuntimeException("Fault not found"));

        if (component.getPossibleFaults() == null) component.setPossibleFaults(new ArrayList<>());
        component.getPossibleFaults().add(fault);
        componentRepository.save(component);
    }

    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public void removeFaultFromComponent(String componentName, String faultName) {
        Component component = componentRepository.findById(componentName).orElseThrow(() -> new RuntimeException("Component not found"));
        if (component.getPossibleFaults() != null) {
            component.getPossibleFaults().removeIf(f -> f.getName().equals(faultName));
            componentRepository.save(component);
        }
    }

    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public void addCauseToPhenomenon(String phenomenonName, String causeName) {
        FaultPhenomenon phenomenon = faultPhenomenonRepository.findById(phenomenonName).orElseThrow(() -> new RuntimeException("Phenomenon not found"));
        FaultCause cause = faultCauseRepository.findById(causeName).orElseThrow(() -> new RuntimeException("Cause not found"));

        if (phenomenon.getCauses() == null) phenomenon.setCauses(new ArrayList<>());
        phenomenon.getCauses().add(cause);
        faultPhenomenonRepository.save(phenomenon);
    }

    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public void removeCauseFromPhenomenon(String phenomenonName, String causeName) {
        FaultPhenomenon phenomenon = faultPhenomenonRepository.findById(phenomenonName).orElseThrow(() -> new RuntimeException("Phenomenon not found"));
        if (phenomenon.getCauses() != null) {
            phenomenon.getCauses().removeIf(c -> c.getName().equals(causeName));
            faultPhenomenonRepository.save(phenomenon);
        }
    }

    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public void addSolutionToCause(String causeName, String solutionName) {
        FaultCause cause = faultCauseRepository.findById(causeName).orElseThrow(() -> new RuntimeException("Cause not found"));
        Solution solution = solutionRepository.findById(solutionName).orElseThrow(() -> new RuntimeException("Solution not found"));

        if (cause.getSolutions() == null) cause.setSolutions(new ArrayList<>());
        cause.getSolutions().add(solution);
        faultCauseRepository.save(cause);
    }

    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
    public void removeSolutionFromCause(String causeName, String solutionName) {
        FaultCause cause = faultCauseRepository.findById(causeName).orElseThrow(() -> new RuntimeException("Cause not found"));
        if (cause.getSolutions() != null) {
            cause.getSolutions().removeIf(s -> s.getName().equals(solutionName));
            faultCauseRepository.save(cause);
        }
    }

    // --- Graph Data & Analysis ---

    @Cacheable(value = "graphData")
    public GraphData getWholeGraph() {
        String nodesQuery = "MATCH (n) RETURN n, labels(n)[0] as label";
        List<GraphNode> nodes = new ArrayList<>(neo4jClient.query(nodesQuery)
                .fetchAs(GraphNode.class)
                .mappedBy((typeSystem, record) -> {
                    var n = record.get("n").asNode();
                    return new GraphNode(
                            n.get("name").asString(),
                            n.get("name").asString(),
                            record.get("label").asString(),
                            n.get("description").asString(null),
                            n.get("attributes").asString(null)
                    );
                })
                .all());

        String linksQuery = "MATCH (n)-[r]->(m) RETURN n.name as source, m.name as target, type(r) as type";
        List<GraphLink> links = new ArrayList<>(neo4jClient.query(linksQuery)
                .fetchAs(GraphLink.class)
                .mappedBy((typeSystem, record) -> new GraphLink(
                        record.get("source").asString(),
                        record.get("target").asString(),
                        record.get("type").asString()
                ))
                .all());

        return new GraphData(nodes, links);
    }

    @Cacheable(value = "graphData", key = "#phenomenonName")
    public GraphData getDiagnosis(String phenomenonName) {
        String nodesQuery = "MATCH path = (p:FaultPhenomenon {name: $name})-[*0..2]->(leaf) " +
                "UNWIND nodes(path) as n " +
                "RETURN DISTINCT n, labels(n)[0] as label";

        List<GraphNode> nodes = new ArrayList<>(neo4jClient.query(nodesQuery)
                .bind(phenomenonName).to("name")
                .fetchAs(GraphNode.class)
                .mappedBy((typeSystem, record) -> {
                    var n = record.get("n").asNode();
                    return new GraphNode(
                            n.get("name").asString(),
                            n.get("name").asString(),
                            record.get("label").asString(),
                            n.get("description").asString(null),
                            n.get("attributes").asString(null)
                    );
                })
                .all());

        String linksQuery = "MATCH path = (p:FaultPhenomenon {name: $name})-[*0..2]->(leaf) " +
                "UNWIND relationships(path) as r " +
                "RETURN DISTINCT startNode(r).name as source, endNode(r).name as target, type(r) as type";

        List<GraphLink> links = new ArrayList<>(neo4jClient.query(linksQuery)
                .bind(phenomenonName).to("name")
                .fetchAs(GraphLink.class)
                .mappedBy((typeSystem, record) -> new GraphLink(
                        record.get("source").asString(),
                        record.get("target").asString(),
                        record.get("type").asString()
                ))
                .all());

        return new GraphData(nodes, links);
    }
}
