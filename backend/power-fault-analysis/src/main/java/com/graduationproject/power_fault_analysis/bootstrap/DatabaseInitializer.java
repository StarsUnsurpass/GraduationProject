package com.graduationproject.power_fault_analysis.bootstrap;

import com.graduationproject.power_fault_analysis.model.DeviceType;
import com.graduationproject.power_fault_analysis.model.FaultCause;
import com.graduationproject.power_fault_analysis.model.FaultPhenomenon;
import com.graduationproject.power_fault_analysis.model.Solution;
import com.graduationproject.power_fault_analysis.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final DeviceTypeRepository deviceTypeRepository;
    private final ComponentRepository componentRepository;
    private final FaultPhenomenonRepository faultPhenomenonRepository;
    private final FaultCauseRepository faultCauseRepository;
    private final SolutionRepository solutionRepository;

    public DatabaseInitializer(DeviceTypeRepository deviceTypeRepository,
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

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (deviceTypeRepository.count() > 0) {
            System.out.println("Database already initialized.");
            return;
        }

        System.out.println("Initializing database with sample data...");

        // 1. Create Solutions
        Solution sol1 = new Solution("更换线圈");
        Solution sol2 = new Solution("清洗散热器");
        solutionRepository.saveAll(List.of(sol1, sol2));

        // 2. Create Fault Causes
        FaultCause cause1 = new FaultCause("匝间短路", new ArrayList<>(List.of(sol1)));
        FaultCause cause2 = new FaultCause("散热器堵塞", new ArrayList<>(List.of(sol2)));
        faultCauseRepository.saveAll(List.of(cause1, cause2));

        // 3. Create Fault Phenomena
        FaultPhenomenon phen1 = new FaultPhenomenon("油温过高", new ArrayList<>(List.of(cause1, cause2)));
        FaultPhenomenon phen2 = new FaultPhenomenon("异响", new ArrayList<>(List.of(cause1)));
        faultPhenomenonRepository.saveAll(List.of(phen1, phen2));

        // 4. Create Components
        com.graduationproject.power_fault_analysis.model.Component comp1 = new com.graduationproject.power_fault_analysis.model.Component("油箱", new ArrayList<>(List.of(phen1)));
        com.graduationproject.power_fault_analysis.model.Component comp2 = new com.graduationproject.power_fault_analysis.model.Component("绕组", new ArrayList<>(List.of(phen1, phen2)));
        componentRepository.saveAll(List.of(comp1, comp2));

        // 5. Create Device Types
        DeviceType device1 = new DeviceType("变压器", new ArrayList<>(List.of(comp1, comp2)));
        deviceTypeRepository.save(device1);

        System.out.println("Database initialization completed.");
    }
}