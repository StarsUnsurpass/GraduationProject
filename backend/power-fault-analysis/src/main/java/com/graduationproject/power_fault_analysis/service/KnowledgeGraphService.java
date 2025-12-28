package com.graduationproject.power_fault_analysis.service;

import com.graduationproject.power_fault_analysis.dto.GraphData;
import com.graduationproject.power_fault_analysis.dto.GraphLink;
import com.graduationproject.power_fault_analysis.dto.GraphNode;
import com.graduationproject.power_fault_analysis.model.*;
import com.graduationproject.power_fault_analysis.repository.*;
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

    public KnowledgeGraphService(DeviceTypeRepository deviceTypeRepository,
                                 ComponentRepository componentRepository,
                                 FaultPhenomenonRepository faultPhenomenonRepository,
                                 FaultCauseRepository faultCauseRepository,
                                 SolutionRepository solutionRepository) {
        this.deviceTypeRepository = deviceTypeRepository;
        this.componentRepository = componentRepository;
        this.faultPhenomenonRepository = faultPhenomenonRepository;
        this.faultCauseRepository = faultCauseRepository;
        this.solutionRepository = solutionRepository;
    }

    // --- Basic CRUD ---

    // DeviceType
    @Transactional
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
    public Solution saveSolution(Solution solution) {
        return solutionRepository.save(solution);
    }

    public Optional<Solution> findSolutionByName(String name) {
        return solutionRepository.findById(name);
    }

    public List<Solution> findAllSolutions() {
        return solutionRepository.findAll();
    }

    // --- Relationship Management ---

    @Transactional
    public void addComponentToDeviceType(String deviceName, String componentName) {
        DeviceType device = deviceTypeRepository.findById(deviceName).orElseThrow(() -> new RuntimeException("Device not found"));
        Component component = componentRepository.findById(componentName).orElseThrow(() -> new RuntimeException("Component not found"));

        if (device.getComponents() == null) device.setComponents(new ArrayList<>());
        device.getComponents().add(component);
        deviceTypeRepository.save(device);
    }

    @Transactional
    public void addFaultToComponent(String componentName, String faultName) {
        Component component = componentRepository.findById(componentName).orElseThrow(() -> new RuntimeException("Component not found"));
        FaultPhenomenon fault = faultPhenomenonRepository.findById(faultName).orElseThrow(() -> new RuntimeException("Fault not found"));

        if (component.getPossibleFaults() == null) component.setPossibleFaults(new ArrayList<>());
        component.getPossibleFaults().add(fault);
        componentRepository.save(component);
    }

    @Transactional
    public void addCauseToPhenomenon(String phenomenonName, String causeName) {
        FaultPhenomenon phenomenon = faultPhenomenonRepository.findById(phenomenonName).orElseThrow(() -> new RuntimeException("Phenomenon not found"));
        FaultCause cause = faultCauseRepository.findById(causeName).orElseThrow(() -> new RuntimeException("Cause not found"));

        if (phenomenon.getCauses() == null) phenomenon.setCauses(new ArrayList<>());
        phenomenon.getCauses().add(cause);
        faultPhenomenonRepository.save(phenomenon);
    }

    @Transactional
    public void addSolutionToCause(String causeName, String solutionName) {
        FaultCause cause = faultCauseRepository.findById(causeName).orElseThrow(() -> new RuntimeException("Cause not found"));
        Solution solution = solutionRepository.findById(solutionName).orElseThrow(() -> new RuntimeException("Solution not found"));

        if (cause.getSolutions() == null) cause.setSolutions(new ArrayList<>());
        cause.getSolutions().add(solution);
        faultCauseRepository.save(cause);
    }

    // --- Graph Data & Analysis ---

    public GraphData getWholeGraph() {
        Set<GraphNode> nodes = new HashSet<>();
        Set<GraphLink> links = new HashSet<>();

        // Helper to add node
        // (Set handles duplicates based on equals/hashCode. Lombok @Data generates them based on all fields.
        // So strict duplicates are handled. If name matches but category differs (unlikely), it might duplicate.
        // For safety, we can trust the Set.)

        // 1. DeviceTypes
        for (DeviceType d : deviceTypeRepository.findAll()) {
            nodes.add(new GraphNode(d.getName(), d.getName(), "DeviceType"));
            if (d.getComponents() != null) {
                for (Component c : d.getComponents()) {
                    nodes.add(new GraphNode(c.getName(), c.getName(), "Component"));
                    links.add(new GraphLink(d.getName(), c.getName(), "HAS_COMPONENT"));
                }
            }
        }

        // 2. Components
        for (Component c : componentRepository.findAll()) {
            nodes.add(new GraphNode(c.getName(), c.getName(), "Component"));
            if (c.getPossibleFaults() != null) {
                for (FaultPhenomenon p : c.getPossibleFaults()) {
                    nodes.add(new GraphNode(p.getName(), p.getName(), "FaultPhenomenon"));
                    links.add(new GraphLink(c.getName(), p.getName(), "HAS_POSSIBLE_FAULT"));
                }
            }
        }

        // 3. FaultPhenomena
        for (FaultPhenomenon p : faultPhenomenonRepository.findAll()) {
            nodes.add(new GraphNode(p.getName(), p.getName(), "FaultPhenomenon"));
            if (p.getCauses() != null) {
                for (FaultCause c : p.getCauses()) {
                    nodes.add(new GraphNode(c.getName(), c.getName(), "FaultCause"));
                    links.add(new GraphLink(p.getName(), c.getName(), "CAUSED_BY"));
                }
            }
        }

        // 4. FaultCauses
        for (FaultCause c : faultCauseRepository.findAll()) {
            nodes.add(new GraphNode(c.getName(), c.getName(), "FaultCause"));
            if (c.getSolutions() != null) {
                for (Solution s : c.getSolutions()) {
                    nodes.add(new GraphNode(s.getName(), s.getName(), "Solution"));
                    links.add(new GraphLink(c.getName(), s.getName(), "SOLVED_BY"));
                }
            }
        }

        // 5. Solutions
        for (Solution s : solutionRepository.findAll()) {
            nodes.add(new GraphNode(s.getName(), s.getName(), "Solution"));
        }

        return new GraphData(new ArrayList<>(nodes), new ArrayList<>(links));
    }

    public GraphData getDiagnosis(String phenomenonName) {
        Optional<FaultPhenomenon> opt = faultPhenomenonRepository.findById(phenomenonName);
        if (opt.isEmpty()) return new GraphData(new ArrayList<>(), new ArrayList<>());

        FaultPhenomenon p = opt.get();
        Set<GraphNode> nodes = new HashSet<>();
        Set<GraphLink> links = new HashSet<>();

        nodes.add(new GraphNode(p.getName(), p.getName(), "FaultPhenomenon"));

        // Use a recursive search or just depth 2 (Cause -> Solution)
        // Since SDN fetch depth is usually 1, we might need to fetch causes explicitly to get their solutions.
        
        if (p.getCauses() != null) {
            for (FaultCause cStub : p.getCauses()) {
                // Fetch full cause to get solutions
                Optional<FaultCause> causeOpt = faultCauseRepository.findById(cStub.getName());
                if (causeOpt.isPresent()) {
                    FaultCause c = causeOpt.get();
                    nodes.add(new GraphNode(c.getName(), c.getName(), "FaultCause"));
                    links.add(new GraphLink(p.getName(), c.getName(), "CAUSED_BY"));
                    
                    if (c.getSolutions() != null) {
                        for (Solution s : c.getSolutions()) {
                            nodes.add(new GraphNode(s.getName(), s.getName(), "Solution"));
                            links.add(new GraphLink(c.getName(), s.getName(), "SOLVED_BY"));
                        }
                    }
                }
            }
        }

        return new GraphData(new ArrayList<>(nodes), new ArrayList<>(links));
    }
}