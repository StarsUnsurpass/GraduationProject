package com.graduationproject.power_fault_analysis.service;

import com.graduationproject.power_fault_analysis.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@Service
public class DataImportService {

    private final KnowledgeGraphService knowledgeGraphService;

    public DataImportService(KnowledgeGraphService knowledgeGraphService) {
        this.knowledgeGraphService = knowledgeGraphService;
    }

    public void importData(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            // 1. Import Nodes
            importNodes(workbook.getSheet("DeviceType"), "DeviceType");
            importNodes(workbook.getSheet("Component"), "Component");
            importNodes(workbook.getSheet("FaultPhenomenon"), "FaultPhenomenon");
            importNodes(workbook.getSheet("FaultCause"), "FaultCause");
            importNodes(workbook.getSheet("Solution"), "Solution");

            // 2. Import Relationships
            importRelationships(workbook.getSheet("Relationships"));
        }
    }

    private void importNodes(Sheet sheet, String type) {
        if (sheet == null) return;
        Iterator<Row> rows = sheet.iterator();
        
        // Skip header if it exists (assuming first row is header "Name")
        if (rows.hasNext()) {
            Row header = rows.next();
            // Simple check: if cell 0 is "Name", it's a header. 
            // If not, we might process it, but usually header exists.
        }

        while (rows.hasNext()) {
            Row row = rows.next();
            Cell cell = row.getCell(0);
            if (cell == null) continue;
            
            String name = cell.getStringCellValue();
            if (name == null || name.trim().isEmpty()) continue;
            name = name.trim();

            switch (type) {
                case "DeviceType":
                    DeviceType d = new DeviceType();
                    d.setName(name);
                    knowledgeGraphService.saveDeviceType(d);
                    break;
                case "Component":
                    Component c = new Component();
                    c.setName(name);
                    knowledgeGraphService.saveComponent(c);
                    break;
                case "FaultPhenomenon":
                    FaultPhenomenon fp = new FaultPhenomenon();
                    fp.setName(name);
                    knowledgeGraphService.saveFaultPhenomenon(fp);
                    break;
                case "FaultCause":
                    FaultCause fc = new FaultCause();
                    fc.setName(name);
                    knowledgeGraphService.saveFaultCause(fc);
                    break;
                case "Solution":
                    Solution s = new Solution();
                    s.setName(name);
                    knowledgeGraphService.saveSolution(s);
                    break;
            }
        }
    }

    private void importRelationships(Sheet sheet) {
        if (sheet == null) return;
        Iterator<Row> rows = sheet.iterator();

        // Skip header: Source | Target | Type
        if (rows.hasNext()) rows.next();

        while (rows.hasNext()) {
            Row row = rows.next();
            if (row.getCell(0) == null || row.getCell(1) == null || row.getCell(2) == null) continue;

            String source = row.getCell(0).getStringCellValue().trim();
            String target = row.getCell(1).getStringCellValue().trim();
            String type = row.getCell(2).getStringCellValue().trim();

            if (source.isEmpty() || target.isEmpty() || type.isEmpty()) continue;

            try {
                switch (type.toUpperCase()) {
                    case "HAS_COMPONENT":
                        knowledgeGraphService.addComponentToDeviceType(source, target);
                        break;
                    case "HAS_POSSIBLE_FAULT":
                        knowledgeGraphService.addFaultToComponent(source, target);
                        break;
                    case "CAUSED_BY":
                        knowledgeGraphService.addCauseToPhenomenon(source, target);
                        break;
                    case "SOLVED_BY":
                        knowledgeGraphService.addSolutionToCause(source, target);
                        break;
                }
            } catch (Exception e) {
                // Log error but continue
                System.err.println("Failed to import relationship: " + source + " -> " + target + " (" + type + ")");
                e.printStackTrace();
            }
        }
    }
}
