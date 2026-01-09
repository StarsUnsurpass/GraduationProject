package com.graduationproject.power_fault_analysis.service;

import com.graduationproject.power_fault_analysis.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class DataImportService {

    private final KnowledgeGraphService knowledgeGraphService;
    private final Neo4jClient neo4jClient;

    public DataImportService(KnowledgeGraphService knowledgeGraphService, Neo4jClient neo4jClient) {
        this.knowledgeGraphService = knowledgeGraphService;
        this.neo4jClient = neo4jClient;
    }

    @Transactional
    @CacheEvict(value = "graphData", allEntries = true)
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

    private void importNodes(Sheet sheet, String label) {
        if (sheet == null) return;
        List<Map<String, Object>> nodeData = new ArrayList<>();
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
            String attributes = getCellValueAsString(row.getCell(2));

            if (name == null || name.isEmpty()) continue;

            Map<String, Object> props = new HashMap<>();
            props.put("name", name);
            props.put("description", description);
            props.put("attributes", attributes);
            nodeData.add(props);
        }

        if (!nodeData.isEmpty()) {
            String query = String.format("UNWIND $props AS map MERGE (n:%s {name: map.name}) SET n += map", label);
            neo4jClient.query(query)
                    .bind(nodeData).to("props")
                    .run();
        }
    }

    private void importRelationships(Sheet sheet) {
        if (sheet == null) return;
        Iterator<Row> rows = sheet.iterator();

        // Skip header
        if (rows.hasNext()) rows.next();

        Map<String, List<Map<String, String>>> relsByType = new HashMap<>();

        while (rows.hasNext()) {
            Row row = rows.next();
            if (row == null) continue;

            String source = getCellValueAsString(row.getCell(0));
            String target = getCellValueAsString(row.getCell(1));
            String type = getCellValueAsString(row.getCell(2));

            if (source.isEmpty() || target.isEmpty() || type.isEmpty()) continue;

            relsByType.computeIfAbsent(type.toUpperCase(), k -> new ArrayList<>())
                    .add(Map.of("source", source, "target", target));
        }

        for (Map.Entry<String, List<Map<String, String>>> entry : relsByType.entrySet()) {
            String type = entry.getKey();
            List<Map<String, String>> rels = entry.getValue();

            // Generic MATCH using property 'name'. Requires indexes on all node labels' 'name' property.
            String query = String.format(
                    "UNWIND $rels AS rel " +
                    "MATCH (s {name: rel.source}), (t {name: rel.target}) " +
                    "MERGE (s)-[r:%s]->(t)", type);

            neo4jClient.query(query)
                    .bind(rels).to("rels")
                    .run();
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
