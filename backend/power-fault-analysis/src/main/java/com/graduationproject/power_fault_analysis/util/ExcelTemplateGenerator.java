package com.graduationproject.power_fault_analysis.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelTemplateGenerator {

    public static void main(String[] args) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        // 1. Device Types
        createSheet(workbook, "DeviceType", new String[]{"Name", "Description"}, new String[][]{
            {"变压器", "用于变换电压的设备"}, 
            {"断路器", "用于切断电路的设备"}, 
            {"隔离开关", ""},
            {"气体绝缘开关设备(GIS)", ""},
            {"电力电缆", ""},
            {"架空线路", ""}
        });

        // 2. Components
        createSheet(workbook, "Component", new String[]{"Name", "Description"}, new String[][]{
            // Transformer
            {"绕组", "变压器的核心部件"}, {"铁芯", ""}, {"绝缘油", ""}, {"分接开关", ""}, {"套管", ""},
            // Breaker/GIS
            {"触头", ""}, {"灭弧室", ""}, {"操作机构", ""}, {"SF6气体系统", ""},
            // Cable/Line
            {"电缆绝缘层", ""}, {"电缆接头", ""}, {"绝缘子串", ""}, {"导线", ""}
        });

        // 3. Fault Phenomena
        createSheet(workbook, "FaultPhenomenon", new String[]{"Name", "Description"}, new String[][]{
            // Transformer
            {"油温过高", "油温表指示超过规定值"}, {"瓦斯保护动作", ""}, {"异响", ""}, {"油位异常", ""}, {"套管闪络", ""},
            // Breaker/GIS
            {"无法合闸", ""}, {"SF6气压低报警", ""}, {"局部放电超标", ""}, {"触头过热", ""},
            // Cable/Line
            {"电缆接头温度高", ""}, {"绝缘电阻低", ""}, {"导线断股", ""}, {"绝缘子击穿", ""}
        });

        // 4. Fault Causes
        createSheet(workbook, "FaultCause", new String[]{"Name", "Description"}, new String[][]{
            // Transformer
            {"过负荷", "长时间超过额定容量运行"}, {"内部短路", ""}, {"油位不足", ""}, {"冷却系统故障", ""}, {"匝间短路", ""},
            // Breaker/GIS
            {"机构卡涩", ""}, {"密封圈老化", ""}, {"气体泄漏", ""}, {"接触电阻过大", ""}, {"绝缘杂质", ""},
            // Cable/Line
            {"接头压接不紧", ""}, {"绝缘受潮老化", ""}, {"外力破坏", ""}, {"雷击过电压", ""}
        });

        // 5. Solutions
        createSheet(workbook, "Solution", new String[]{"Name", "Description"}, new String[][]{
            // Transformer
            {"检查负荷状态", "调整运行方式"}, {"过滤或更换绝缘油", ""}, {"补油", ""}, {"维修冷却风扇", ""}, {"更换线圈", ""},
            // Breaker/GIS
            {"加注润滑油", ""}, {"更换密封圈", ""}, {"补充SF6气体", ""}, {"打磨触头并紧固", ""}, {"解体检修清洗", ""},
            // Cable/Line
            {"重新制作接头", ""}, {"更换电缆段", ""}, {"修补导线", ""}, {"更换绝缘子", ""}
        });

        // 6. Relationships: Source | Target | Type
        createSheet(workbook, "Relationships", 
            new String[]{"Source", "Target", "Type"}, 
            new String[][]{
                // --- Transformer ---
                {"变压器", "绕组", "HAS_COMPONENT"},
                {"变压器", "绝缘油", "HAS_COMPONENT"},
                {"变压器", "套管", "HAS_COMPONENT"},
                
                {"绕组", "异响", "HAS_POSSIBLE_FAULT"},
                {"绝缘油", "油温过高", "HAS_POSSIBLE_FAULT"},
                {"绝缘油", "油位异常", "HAS_POSSIBLE_FAULT"},
                {"套管", "套管闪络", "HAS_POSSIBLE_FAULT"},

                {"油温过高", "过负荷", "CAUSED_BY"},
                {"油温过高", "冷却系统故障", "CAUSED_BY"},
                {"油位异常", "油位不足", "CAUSED_BY"},
                {"异响", "匝间短路", "CAUSED_BY"},

                {"过负荷", "检查负荷状态", "SOLVED_BY"},
                {"冷却系统故障", "维修冷却风扇", "SOLVED_BY"},
                {"油位不足", "补油", "SOLVED_BY"},
                {"匝间短路", "更换线圈", "SOLVED_BY"},

                // --- GIS / Breaker ---
                {"气体绝缘开关设备(GIS)", "SF6气体系统", "HAS_COMPONENT"},
                {"气体绝缘开关设备(GIS)", "操作机构", "HAS_COMPONENT"},
                {"断路器", "触头", "HAS_COMPONENT"},

                {"SF6气体系统", "SF6气压低报警", "HAS_POSSIBLE_FAULT"},
                {"操作机构", "无法合闸", "HAS_POSSIBLE_FAULT"},
                {"触头", "触头过热", "HAS_POSSIBLE_FAULT"},

                {"SF6气压低报警", "气体泄漏", "CAUSED_BY"},
                {"气体泄漏", "密封圈老化", "CAUSED_BY"},
                {"无法合闸", "机构卡涩", "CAUSED_BY"},
                {"触头过热", "接触电阻过大", "CAUSED_BY"},

                {"气体泄漏", "补充SF6气体", "SOLVED_BY"},
                {"密封圈老化", "更换密封圈", "SOLVED_BY"},
                {"机构卡涩", "加注润滑油", "SOLVED_BY"},
                {"接触电阻过大", "打磨触头并紧固", "SOLVED_BY"},

                // --- Power Cable ---
                {"电力电缆", "电缆接头", "HAS_COMPONENT"},
                {"电力电缆", "电缆绝缘层", "HAS_COMPONENT"},

                {"电缆接头", "电缆接头温度高", "HAS_POSSIBLE_FAULT"},
                {"电缆绝缘层", "绝缘电阻低", "HAS_POSSIBLE_FAULT"},

                {"电缆接头温度高", "接头压接不紧", "CAUSED_BY"},
                {"绝缘电阻低", "绝缘受潮老化", "CAUSED_BY"},

                {"接头压接不紧", "重新制作接头", "SOLVED_BY"},
                {"绝缘受潮老化", "更换电缆段", "SOLVED_BY"}
            }
        );

        try (FileOutputStream fileOut = new FileOutputStream("power_fault_template.xlsx")) {
            workbook.write(fileOut);
        }
        workbook.close();
        System.out.println("Excel template generated successfully: power_fault_template.xlsx");
    }

    private static void createSheet(Workbook workbook, String sheetName, String[] headers, String[][] data) {
        Sheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        
        // Header Style
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < data[i].length; j++) {
                row.createCell(j).setCellValue(data[i][j]);
            }
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}