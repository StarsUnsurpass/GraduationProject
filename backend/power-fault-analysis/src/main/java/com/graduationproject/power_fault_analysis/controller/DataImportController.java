package com.graduationproject.power_fault_analysis.controller;

import com.graduationproject.power_fault_analysis.service.DataImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*")
public class DataImportController {

    private final DataImportService dataImportService;

    public DataImportController(DataImportService dataImportService) {
        this.dataImportService = dataImportService;
    }

    @PostMapping("/import")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            dataImportService.importData(file);
            return ResponseEntity.ok("Import successful");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Import failed: " + e.getMessage());
        }
    }
}
