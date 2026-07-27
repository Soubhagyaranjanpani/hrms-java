// File: com/hrms/transfer/application/PdfTransferLetterGenerator.java
package com.hrms.transfer.application;

import com.hrms.transfer.domain.TransferRecord;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfTransferLetterGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public byte[] generateLetter(TransferRecord r) throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                PDFont fontBold = PDType1Font.HELVETICA_BOLD;
                PDFont fontRegular = PDType1Font.HELVETICA;
                PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;

                float pageWidth = PDRectangle.A4.getWidth();
                float pageHeight = PDRectangle.A4.getHeight();
                float margin = 50;
                float contentWidth = pageWidth - 2 * margin;

                float currentY = pageHeight - 40;

                // ========== HEADER / LOGO ==========
                float logoWidth = 160;
                float logoHeight = 50;

                String[] logoPaths = {
                        "/images/arigen-logo-23.webp",
                        "/images/arigen-logo-23.png",
                        "/static/images/arigen-logo-23.webp",
                        "/static/images/arigen-logo-23.png"
                };

                for (String logoPath : logoPaths) {
                    try (InputStream logoStream = getClass().getResourceAsStream(logoPath)) {
                        if (logoStream != null) {
                            BufferedImage bufferedImage = ImageIO.read(logoStream);
                            if (bufferedImage != null) {
                                ByteArrayOutputStream pngBytes = new ByteArrayOutputStream();
                                ImageIO.write(bufferedImage, "png", pngBytes);
                                PDImageXObject logo = PDImageXObject.createFromByteArray(
                                        document, pngBytes.toByteArray(), "logo");

                                float aspectRatio = (float) bufferedImage.getWidth() / bufferedImage.getHeight();
                                logoHeight = logoWidth / aspectRatio;

                                cs.drawImage(logo, margin, currentY - logoHeight, logoWidth, logoHeight);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to load logo from " + logoPath + ": " + e.getMessage());
                    }
                }

                cs.setNonStrokingColor(new java.awt.Color(0, 0, 0));
                cs.setFont(fontBold, 16);
                String title = "TRANSFER LETTER";
                float titleWidth = getTextWidth(title, fontBold, 16);
                cs.beginText();
                cs.newLineAtOffset(pageWidth - margin - titleWidth, currentY - 8);
                cs.showText(title);
                cs.endText();

                cs.setFont(fontRegular, 10);
                String orderText = r.getTransferOrderNumber() != null ? "Order No: " + r.getTransferOrderNumber() : "";
                float orderWidth = getTextWidth(orderText, fontRegular, 10);
                cs.beginText();
                cs.newLineAtOffset(pageWidth - margin - orderWidth, currentY - 23);
                cs.showText(orderText);
                cs.endText();

                currentY -= 38;
                cs.setStrokingColor(new java.awt.Color(0, 51, 102));
                cs.setLineWidth(1.5f);
                cs.moveTo(margin, currentY);
                cs.lineTo(pageWidth - margin, currentY);
                cs.stroke();

                // ========== DATE ==========
                currentY -= 22;
                cs.setNonStrokingColor(new java.awt.Color(0, 0, 0));
                cs.setFont(fontRegular, 10);
                String dateLine = "Date: " + (r.getTransferDate() != null ? r.getTransferDate().format(DATE_FORMAT) : "—");
                cs.beginText();
                cs.newLineAtOffset(margin, currentY);
                cs.showText(dateLine);
                cs.endText();

                // ========== EMPLOYEE INFO ==========
                currentY -= 24;
                String empName = safe(r.getEmployee() != null ? r.getEmployee().getFullName() : null);
                String empCode = safe(r.getEmployee() != null ? r.getEmployee().getEmployeeCode() : null);
                String desigName = safe(r.getDesignationName());
                String fromBranchName = safe(r.getFromBranch() != null ? r.getFromBranch().getName() : null);
                String toBranchName = safe(r.getToBranch() != null ? r.getToBranch().getName() : null);

                drawInfoRow(cs, fontRegular, fontBold, margin, currentY, "Employee Name", empName);
                currentY -= 18;
                drawInfoRow(cs, fontRegular, fontBold, margin, currentY, "Employee Code", empCode);
                currentY -= 18;
                drawInfoRow(cs, fontRegular, fontBold, margin, currentY, "Designation", desigName);

                // ========== SALUTATION / BODY ==========
                currentY -= 30;
                cs.setFont(fontRegular, 10.5f);
                String salutation = "Dear " + (empName.equals("—") ? "Employee" : empName) + ",";
                cs.beginText();
                cs.newLineAtOffset(margin, currentY);
                cs.showText(salutation);
                cs.endText();

                currentY -= 20;
                String bodyPara = "We would like to inform you that, as per organizational requirement, you are being "
                        + "transferred from " + fromBranchName + " to " + toBranchName
                        + ", effective " + (r.getEffectiveDate() != null ? r.getEffectiveDate().format(DATE_FORMAT) : "—")
                        + ". The details of your transfer are set out below.";
                currentY = drawWrappedText(cs, fontRegular, 10, bodyPara, margin, currentY, contentWidth, 14);

                // ========== TRANSFER DETAILS TABLE ==========
                currentY -= 20;
                float tableStartY = currentY;
                float col1X = margin;
                float col2X = margin + contentWidth * 0.38f;
                float col3X = margin + contentWidth * 0.69f;

                cs.setNonStrokingColor(new java.awt.Color(220, 230, 242));
                cs.addRect(margin, tableStartY - 14, contentWidth, 18);
                cs.fill();

                cs.setNonStrokingColor(new java.awt.Color(0, 51, 102));
                cs.setFont(fontBold, 9.5f);
                cs.beginText(); cs.newLineAtOffset(col1X + 5, tableStartY - 10); cs.showText("Particulars"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(col2X + 5, tableStartY - 10); cs.showText("From"); cs.endText();
                cs.beginText(); cs.newLineAtOffset(col3X + 5, tableStartY - 10); cs.showText("To"); cs.endText();

                String[][] rows = {
                        {"Department", safe(r.getFromDepartment() != null ? r.getFromDepartment().getName() : null),
                                safe(r.getToDepartment() != null ? r.getToDepartment().getName() : null)},
                        {"Branch", fromBranchName, toBranchName},
                        {"Transfer Type", "-", safe(r.getTransferType())},
                        {"Effective Date", "-", r.getEffectiveDate() != null ? r.getEffectiveDate().format(DATE_FORMAT) : "—"},
                };

                float rowY = tableStartY - 32;
                float rowHeight = 18;
                for (int i = 0; i < rows.length; i++) {
                    float y = rowY - (i * rowHeight);
                    if (i % 2 == 0) {
                        cs.setNonStrokingColor(new java.awt.Color(248, 250, 252));
                        cs.addRect(margin, y - 11, contentWidth, rowHeight);
                        cs.fill();
                    }
                    cs.setNonStrokingColor(new java.awt.Color(0, 0, 0));
                    cs.setFont(fontBold, 9);
                    cs.beginText(); cs.newLineAtOffset(col1X + 5, y - 3); cs.showText(rows[i][0]); cs.endText();
                    cs.setFont(fontRegular, 9);
                    cs.beginText(); cs.newLineAtOffset(col2X + 5, y - 3); cs.showText(rows[i][1]); cs.endText();
                    cs.beginText(); cs.newLineAtOffset(col3X + 5, y - 3); cs.showText(rows[i][2]); cs.endText();
                }

                float lineY = rowY - (rows.length * rowHeight) - 8;
                cs.setStrokingColor(new java.awt.Color(0, 51, 102));
                cs.setLineWidth(1f);
                cs.moveTo(margin, lineY + 8);
                cs.lineTo(pageWidth - margin, lineY + 8);
                cs.stroke();

                // ========== REASON ==========
                float reasonY = lineY - 20;
                if (r.getTransferReason() != null && !r.getTransferReason().isBlank()) {
                    cs.setNonStrokingColor(new java.awt.Color(0, 0, 0));
                    cs.setFont(fontBold, 9.5f);
                    cs.beginText(); cs.newLineAtOffset(margin, reasonY); cs.showText("Reason:"); cs.endText();
                    reasonY = drawWrappedText(cs, fontRegular, 9.5f, r.getTransferReason(), margin, reasonY - 14, contentWidth, 13);
                }

                // ========== CLOSING ==========
                float closeY = reasonY - 25;
                cs.setNonStrokingColor(new java.awt.Color(0, 0, 0));
                cs.setFont(fontRegular, 10);
                cs.beginText();
                cs.newLineAtOffset(margin, closeY);
                cs.showText("We wish you success in your new role and look forward to your continued contribution.");
                cs.endText();

                // ========== FOOTER ==========
                float footerY = 45;
                cs.setStrokingColor(new java.awt.Color(200, 200, 200));
                cs.setLineWidth(0.5f);
                cs.moveTo(margin, footerY + 5);
                cs.lineTo(pageWidth - margin, footerY + 5);
                cs.stroke();

                cs.setNonStrokingColor(new java.awt.Color(120, 120, 120));
                cs.setFont(fontItalic, 8);
                String footer = "This is a computer generated transfer letter, no signature is required.";
                float footerWidth = getTextWidth(footer, fontItalic, 8);
                cs.beginText();
                cs.newLineAtOffset((pageWidth - footerWidth) / 2, footerY - 5);
                cs.showText(footer);
                cs.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private void drawInfoRow(PDPageContentStream cs, PDFont fontRegular, PDFont fontBold,
                             float x, float y, String label, String value) throws Exception {
        float labelWidth = 120;
        cs.setNonStrokingColor(new java.awt.Color(80, 80, 80));
        cs.setFont(fontRegular, 9);
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(label + ":");
        cs.endText();

        cs.setNonStrokingColor(new java.awt.Color(0, 0, 0));
        cs.setFont(fontBold, 10);
        cs.beginText();
        cs.newLineAtOffset(x + labelWidth, y);
        cs.showText(value);
        cs.endText();
    }

    private float drawWrappedText(PDPageContentStream cs, PDFont font, float fontSize,
                                  String text, float x, float y, float maxWidth, float lineHeight) throws Exception {
        List<String> words = List.of(text.split("\\s+"));
        StringBuilder line = new StringBuilder();
        float currentY = y;

        cs.setNonStrokingColor(new java.awt.Color(20, 20, 20));
        cs.setFont(font, fontSize);

        for (String word : words) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (getTextWidth(candidate, font, fontSize) > maxWidth) {
                cs.beginText();
                cs.newLineAtOffset(x, currentY);
                cs.showText(line.toString());
                cs.endText();
                currentY -= lineHeight;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (!line.isEmpty()) {
            cs.beginText();
            cs.newLineAtOffset(x, currentY);
            cs.showText(line.toString());
            cs.endText();
            currentY -= lineHeight;
        }
        return currentY;
    }

    private String safe(String str) {
        if (str == null || str.trim().isEmpty()) return "—";
        return str;
    }

    private float getTextWidth(String text, PDFont font, float fontSize) throws Exception {
        return font.getStringWidth(text) / 1000 * fontSize;
    }
}
