package com.graduationproject.power_fault_analysis.service;

import com.graduationproject.power_fault_analysis.model.*;
import com.graduationproject.power_fault_analysis.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    // DeviceType operations
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

    // Component operations
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

    // FaultPhenomenon operations
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

    // FaultCause operations
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

    // Solution operations
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

    // TODO: Add methods for creating and managing relationships between entities.
    // E.g., addComponentToDeviceType(DeviceType deviceType, Component component)
}
