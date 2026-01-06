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
        
        // Skip header
        if (rows.hasNext()) {
            rows.next();
        }

        while (rows.hasNext()) {
            Row row = rows.next();
            if (row == null) continue;
            
            String name = getCellValueAsString(row.getCell(0));
            String description = getCellValueAsString(row.getCell(1));
            String attributes = getCellValueAsString(row.getCell(2)); // Read attributes from 3rd column
            
            if (name == null || name.isEmpty()) continue;

            switch (type) {
                case "DeviceType":
                    DeviceType d = new DeviceType();
                    d.setName(name);
                    d.setDescription(description);
                    d.setAttributes(attributes);
                    knowledgeGraphService.saveDeviceType(d);
                    break;
                case "Component":
                    Component c = new Component();
                    c.setName(name);
                    c.setDescription(description);
                    c.setAttributes(attributes);
                    knowledgeGraphService.saveComponent(c);
                    break;
                case "FaultPhenomenon":
                    FaultPhenomenon fp = new FaultPhenomenon();
                    fp.setName(name);
                    fp.setDescription(description);
                    fp.setAttributes(attributes);
                    knowledgeGraphService.saveFaultPhenomenon(fp);
                    break;
                case "FaultCause":
                    FaultCause fc = new FaultCause();
                    fc.setName(name);
                    fc.setDescription(description);
                    fc.setAttributes(attributes);
                    knowledgeGraphService.saveFaultCause(fc);
                    break;
                case "Solution":
                    Solution s = new Solution();
                    s.setName(name);
                    s.setDescription(description);
                    s.setAttributes(attributes);
                    knowledgeGraphService.saveSolution(s);
                    break;
            }
        }
    }

    private void importRelationships(Sheet sheet) {
        if (sheet == null) return;
        Iterator<Row> rows = sheet.iterator();

        // Skip header
        if (rows.hasNext()) rows.next();

        while (rows.hasNext()) {
            Row row = rows.next();
            if (row == null) continue;

            String source = getCellValueAsString(row.getCell(0));
            String target = getCellValueAsString(row.getCell(1));
            String type = getCellValueAsString(row.getCell(2));

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
                System.err.println("Failed to import relationship: " + source + " -> " + target + " (" + type + ")");
                e.printStackTrace();
            }
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Avoid scientific notation for integers
                    double val = cell.getNumericCellValue();
                    if (val == (long) val) {
                        return String.format("%d", (long) val);
                    } else {
                        return String.valueOf(val);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (IllegalStateException e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BLANK:
            case _NONE:
            case ERROR:
            default:
                return "";
        }
    }
}
