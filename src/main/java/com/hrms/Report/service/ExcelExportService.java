package com.hrms.Report.service;



import com.hrms.Report.dto.ReportResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class ExcelExportService {

    public byte[] exportToExcel(ReportResponseDTO reportData) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(reportData.getReportName());

            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            Map<String, CellStyle> statusStyles = createStatusStyles(workbook);

            int rowNum = 0;

            // Title
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(reportData.getReportName());
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, reportData.getColumns().size()));
            rowNum++;

            // Metadata
            Row metaRow = sheet.createRow(rowNum++);
            metaRow.createCell(0).setCellValue("Generated: " +
                    reportData.getGeneratedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            metaRow.createCell(1).setCellValue("Total Records: " + reportData.getSummary().getTotalRecords());
            rowNum++;

            // Headers
            Row headerRow = sheet.createRow(rowNum++);
            Cell slHeader = headerRow.createCell(0);
            slHeader.setCellValue("Sl. No");
            slHeader.setCellStyle(headerStyle);

            List<ReportResponseDTO.ColumnDefinition> columns = reportData.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i + 1);
                cell.setCellValue(columns.get(i).getHeader());
                cell.setCellStyle(headerStyle);
            }

            // Data
            List<Map<String, Object>> data = reportData.getData();
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(rowNum++);
                Map<String, Object> rowData = data.get(i);

                Cell slCell = row.createCell(0);
                slCell.setCellValue(i + 1);
                slCell.setCellStyle(dataStyle);

                for (int j = 0; j < columns.size(); j++) {
                    Cell cell = row.createCell(j + 1);
                    setCellValue(cell, rowData.get(columns.get(j).getField()),
                            columns.get(j).getType(), dataStyle, currencyStyle, statusStyles);
                }
            }

            // Auto-size
            for (int i = 0; i <= columns.size(); i++) sheet.autoSizeColumn(i);
            sheet.createFreezePane(0, 3);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("Excel export error", e);
            throw new RuntimeException("Failed to generate Excel", e);
        }
    }

    private void setCellValue(Cell cell, Object value, String type,
                              CellStyle defaultStyle, CellStyle currencyStyle,
                              Map<String, CellStyle> statusStyles) {
        if (value == null) { cell.setCellValue("-"); cell.setCellStyle(defaultStyle); return; }
        switch (type) {
            case "CURRENCY":
                if (value instanceof Number) { cell.setCellValue(((Number) value).doubleValue()); cell.setCellStyle(currencyStyle); }
                else { cell.setCellValue(value.toString()); cell.setCellStyle(defaultStyle); }
                break;
            case "NUMBER":
                if (value instanceof Number) cell.setCellValue(((Number) value).doubleValue());
                else cell.setCellValue(value.toString());
                cell.setCellStyle(defaultStyle);
                break;
            case "DATE":
                if (value instanceof LocalDate) cell.setCellValue(value.toString());
                else if (value instanceof LocalDateTime) cell.setCellValue(((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                else if (value instanceof LocalTime) cell.setCellValue(value.toString());
                else cell.setCellValue(value.toString());
                cell.setCellStyle(defaultStyle);
                break;
            case "STATUS":
                cell.setCellValue(value.toString());
                cell.setCellStyle(statusStyles.getOrDefault(value.toString().toUpperCase(), defaultStyle));
                break;
            default:
                cell.setCellValue(value.toString());
                cell.setCellStyle(defaultStyle);
        }
    }

    private CellStyle createTitleStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 14);
        f.setColor(IndexedColors.DARK_BLUE.getIndex()); s.setFont(f);
        s.setAlignment(HorizontalAlignment.CENTER);
        return s;
    }

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 11);
        f.setColor(IndexedColors.WHITE.getIndex()); s.setFont(f);
        s.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setBorderBottom(BorderStyle.THIN); s.setBorderTop(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
        return s;
    }

    private CellStyle createDataStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setBorderBottom(BorderStyle.THIN); s.setBorderTop(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
        return s;
    }

    private CellStyle createCurrencyStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.cloneStyleFrom(createDataStyle(wb));
        s.setDataFormat(wb.createDataFormat().getFormat("₹#,##0.00"));
        s.setAlignment(HorizontalAlignment.RIGHT);
        return s;
    }

    private Map<String, CellStyle> createStatusStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<>();
        styles.put("APPROVED", createColoredStyle(wb, IndexedColors.LIGHT_GREEN));
        styles.put("PRESENT", createColoredStyle(wb, IndexedColors.LIGHT_GREEN));
        styles.put("ACTIVE", createColoredStyle(wb, IndexedColors.LIGHT_GREEN));
        styles.put("COMPLETED", createColoredStyle(wb, IndexedColors.LIGHT_GREEN));
        styles.put("PAID", createColoredStyle(wb, IndexedColors.LIGHT_GREEN));
        styles.put("PENDING", createColoredStyle(wb, IndexedColors.LIGHT_ORANGE));
        styles.put("IN_PROGRESS", createColoredStyle(wb, IndexedColors.LIGHT_ORANGE));
        styles.put("DRAFT", createColoredStyle(wb, IndexedColors.GREY_25_PERCENT));
        styles.put("REJECTED", createColoredStyle(wb, IndexedColors.RED));
        styles.put("ABSENT", createColoredStyle(wb, IndexedColors.RED));
        styles.put("INACTIVE", createColoredStyle(wb, IndexedColors.RED));
        return styles;
    }

    private CellStyle createColoredStyle(Workbook wb, IndexedColors color) {
        CellStyle s = wb.createCellStyle();
        s.cloneStyleFrom(createDataStyle(wb));
        s.setFillForegroundColor(color.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return s;
    }
}
