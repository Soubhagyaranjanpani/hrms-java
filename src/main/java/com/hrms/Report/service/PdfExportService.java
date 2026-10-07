package com.hrms.Report.service;




import com.hrms.Report.dto.ReportResponseDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PdfExportService {

    public byte[] exportToPdf(ReportResponseDTO reportData) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            document.setMargins(20, 20, 20, 20);
            PdfWriter writer = PdfWriter.getInstance(document, bos);

            writer.setPageEvent(new PdfPageEventHelper() {
                public void onEndPage(PdfWriter writer, Document document) {
                    try {
                        PdfPTable footer = new PdfPTable(1);
                        footer.setTotalWidth(770);
                        footer.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                        footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                        footer.addCell(new Phrase("Page " + writer.getPageNumber(),
                                FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY)));
                        footer.writeSelectedRows(0, -1, 20, 30, writer.getDirectContent());
                    } catch (Exception ignored) {}
                }
            });

            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new BaseColor(0, 51, 102));
            Paragraph title = new Paragraph(reportData.getReportName(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);

            // Metadata
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);
            document.add(new Paragraph("Generated: " +
                    reportData.getGeneratedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    " | Records: " + reportData.getSummary().getTotalRecords(), metaFont));
            document.add(new Paragraph(" "));

            // Table
            List<ReportResponseDTO.ColumnDefinition> columns = reportData.getColumns();
            PdfPTable table = new PdfPTable(columns.size() + 1);
            table.setWidthPercentage(100);
            table.setHeaderRows(1);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, BaseColor.WHITE);
            BaseColor headerBg = new BaseColor(0, 51, 102);

            PdfPCell slH = new PdfPCell(new Phrase("#", headerFont));
            slH.setBackgroundColor(headerBg); slH.setHorizontalAlignment(Element.ALIGN_CENTER);
            slH.setPadding(4); table.addCell(slH);

            for (ReportResponseDTO.ColumnDefinition col : columns) {
                PdfPCell cell = new PdfPCell(new Phrase(col.getHeader(), headerFont));
                cell.setBackgroundColor(headerBg); cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(4); table.addCell(cell);
            }

            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 7, BaseColor.BLACK);
            List<Map<String, Object>> data = reportData.getData();

            for (int i = 0; i < data.size(); i++) {
                Map<String, Object> row = data.get(i);
                BaseColor bg = i % 2 == 0 ? BaseColor.WHITE : new BaseColor(245, 245, 250);

                PdfPCell sl = new PdfPCell(new Phrase(String.valueOf(i + 1), dataFont));
                sl.setBackgroundColor(bg); sl.setPadding(4); table.addCell(sl);

                for (ReportResponseDTO.ColumnDefinition col : columns) {
                    Object value = row.get(col.getField());
                    String text = formatPdfValue(value, col.getType());
                    PdfPCell dc = new PdfPCell(new Phrase(text, dataFont));
                    dc.setBackgroundColor(getStatusBg(value, col, bg));
                    dc.setPadding(4);
                    if ("CURRENCY".equals(col.getType()) || "NUMBER".equals(col.getType()))
                        dc.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(dc);
                }
            }

            document.add(table);
            document.close();
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("PDF export error", e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private BaseColor getStatusBg(Object value, ReportResponseDTO.ColumnDefinition col, BaseColor defaultBg) {
        if (!"STATUS".equals(col.getType()) || value == null) return defaultBg;
        String s = value.toString().toUpperCase();
        if (List.of("APPROVED","PRESENT","ACTIVE","COMPLETED","PAID").contains(s))
            return new BaseColor(220, 252, 231);
        if (List.of("PENDING","IN_PROGRESS").contains(s))
            return new BaseColor(254, 243, 199);
        if (List.of("REJECTED","ABSENT","INACTIVE").contains(s))
            return new BaseColor(254, 226, 226);
        return defaultBg;
    }

    private String formatPdfValue(Object value, String type) {
        if (value == null) return "-";
        if (value instanceof LocalDate) return ((LocalDate) value).format(DateTimeFormatter.ISO_LOCAL_DATE);
        if (value instanceof LocalDateTime) return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        if (value instanceof LocalTime) return value.toString();
        if (value instanceof Number && "CURRENCY".equals(type))
            return String.format("₹%,.2f", ((Number) value).doubleValue());
        return value.toString();
    }
}
