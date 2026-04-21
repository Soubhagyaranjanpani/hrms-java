package com.hrms.payroll.application;



import com.hrms.payroll.dto.PayrollRecordResponse;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfPayslipGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    // SIDEBAR COLORS - Explicit RGB values
    private static final Color COLOR_PRIMARY = new Color(194, 8, 52);     // #4f46e5 (Indigo-600)
    private static final Color COLOR_PRIMARY_LIGHT = new Color(237, 233, 254); // #ede9fe (Violet-50)
    private static final Color COLOR_VIOLET = new Color(124, 58, 237);     // #7c3aed (Violet-600)

    // Other colors
    private static final Color COLOR_GREEN = new Color(5, 150, 105);       // #059669
    private static final Color COLOR_RED = new Color(168, 140, 9);         // #dc2626
    private static final Color COLOR_AMBER = new Color(217, 119, 6);       // #d97706
    private static final Color COLOR_DARK = new Color(17, 24, 39);         // #111827
    private static final Color COLOR_GRAY = new Color(107, 114, 128);      // #6b7280
    private static final Color COLOR_LIGHT_GRAY = new Color(249, 250, 251); // #f9fafb
    private static final Color COLOR_WHITE = new Color(255, 255, 255);
    private static final Color COLOR_BORDER = new Color(229, 231, 235);    // #e5e7eb

    public byte[] generatePayslip(PayrollRecordResponse record) throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                PDFont fontBold = PDType1Font.HELVETICA_BOLD;
                PDFont fontRegular = PDType1Font.HELVETICA;

                float pageWidth = PDRectangle.A4.getWidth();
                float pageHeight = PDRectangle.A4.getHeight();
                float margin = 50;
                float contentWidth = pageWidth - 2 * margin;

                // ========== TOP COLORED BAR (SIDEBAR COLOR) ==========
                cs.setNonStrokingColor(COLOR_PRIMARY);
                cs.addRect(0, pageHeight - 8, pageWidth, 8);
                cs.fill();

                // ========== HEADER SECTION ==========
                float yPos = pageHeight - 50;

                // Company Name - SIDEBAR COLOR
                cs.setNonStrokingColor(COLOR_PRIMARY);
                cs.setFont(fontBold, 20);
                cs.beginText();
                cs.newLineAtOffset(margin, yPos);
                cs.showText("HRMS");
                cs.endText();

                cs.setNonStrokingColor(COLOR_GRAY);
                cs.setFont(fontRegular, 9);
                cs.beginText();
                cs.newLineAtOffset(margin, yPos - 15);
                cs.showText("Human Resource Management System");
                cs.endText();

                // PAYSLIP Title - SIDEBAR COLOR
                cs.setNonStrokingColor(COLOR_PRIMARY);
                cs.setFont(fontBold, 24);
                String title = "PAYSLIP";
                float titleWidth = getTextWidth(title, fontBold, 24);
                cs.beginText();
                cs.newLineAtOffset(pageWidth - margin - titleWidth, yPos - 5);
                cs.showText(title);
                cs.endText();

                // Month
                cs.setNonStrokingColor(COLOR_GRAY);
                cs.setFont(fontRegular, 11);
                String monthText = record.getPayrollMonth();
                float monthWidth = getTextWidth(monthText, fontRegular, 11);
                cs.beginText();
                cs.newLineAtOffset(pageWidth - margin - monthWidth, yPos - 25);
                cs.showText(monthText);
                cs.endText();

                // Divider line
                yPos -= 50;
                cs.setStrokingColor(COLOR_BORDER);
                cs.setLineWidth(1);
                cs.moveTo(margin, yPos);
                cs.lineTo(pageWidth - margin, yPos);
                cs.stroke();

                // ========== EMPLOYEE INFO CARD WITH SIDEBAR COLOR ==========
                yPos -= 25;
                float cardStartY = yPos;
                float cardHeight = 95;

                // Card background - SIDEBAR LIGHT COLOR
                cs.setNonStrokingColor(COLOR_PRIMARY_LIGHT);
                cs.addRect(margin, cardStartY - cardHeight, contentWidth, cardHeight);
                cs.fill();

                // Card border - SIDEBAR COLOR
                cs.setStrokingColor(COLOR_PRIMARY);
                cs.setLineWidth(1.2f);
                cs.addRect(margin, cardStartY - cardHeight, contentWidth, cardHeight);
                cs.stroke();

                // Avatar Circle - SIDEBAR COLOR
                float avatarX = margin + 20;
                float avatarY = cardStartY - 55;
                float avatarSize = 50;

                cs.setNonStrokingColor(COLOR_PRIMARY);
                drawCircle(cs, avatarX + avatarSize/2, avatarY + avatarSize/2, avatarSize/2);
                cs.fill();

                // Avatar Initials
                String initials = getInitials(record.getEmployee());
                cs.setNonStrokingColor(COLOR_WHITE);
                cs.setFont(fontBold, 20);
                float initWidth = getTextWidth(initials, fontBold, 20);
                cs.beginText();
                cs.newLineAtOffset(avatarX + (avatarSize - initWidth)/2, avatarY + 18);
                cs.showText(initials);
                cs.endText();

                // Employee Name
                float infoX = avatarX + avatarSize + 25;
                cs.setNonStrokingColor(COLOR_DARK);
                cs.setFont(fontBold, 16);
                cs.beginText();
                cs.newLineAtOffset(infoX, cardStartY - 20);
                cs.showText(cleanString(record.getEmployee()));
                cs.endText();

                // Designation and Department
                cs.setNonStrokingColor(COLOR_GRAY);
                cs.setFont(fontRegular, 10);
                cs.beginText();
                cs.newLineAtOffset(infoX, cardStartY - 38);
                cs.showText(cleanString(record.getDesignation()) + "  |  " + cleanString(record.getDepartment()));
                cs.endText();

                // Code and Branch
                cs.beginText();
                cs.newLineAtOffset(infoX, cardStartY - 53);
                cs.showText("Code: " + cleanString(record.getEmployeeCode()) + "  |  Branch: " + cleanString(record.getBranch()));
                cs.endText();

                // Status Badge
                String status = record.getStatus() != null ? record.getStatus() : "DRAFT";
                Color statusColor = getStatusColor(status);
                float badgeWidth = 85;
                float badgeHeight = 24;
                float badgeX = pageWidth - margin - badgeWidth - 10;
                float badgeY = cardStartY - 35;

                cs.setNonStrokingColor(statusColor);
                cs.addRect(badgeX, badgeY, badgeWidth, badgeHeight);
                cs.fill();

                cs.setNonStrokingColor(COLOR_WHITE);
                cs.setFont(fontBold, 9);
                float statusWidth = getTextWidth(status, fontBold, 9);
                cs.beginText();
                cs.newLineAtOffset(badgeX + (badgeWidth - statusWidth)/2, badgeY + 8);
                cs.showText(status);
                cs.endText();

                // ========== STATS ROW WITH COLORED CARDS ==========
                yPos = cardStartY - cardHeight - 25;
                float statCardWidth = (contentWidth - 30) / 4;
                float statCardHeight = 60;

                Color[] statColors = {
                        COLOR_PRIMARY,  // Working Days - Sidebar Purple
                        COLOR_GREEN,    // Paid Days - Green
                        COLOR_RED,      // LOP Days - Red
                        COLOR_AMBER     // Payment Date - Amber
                };

                String[][] stats = {
                        {"Working Days", String.valueOf(record.getWorkingDays() != null ? record.getWorkingDays() : 0)},
                        {"Paid Days", String.valueOf(record.getPaidDays() != null ? record.getPaidDays() : 0)},
                        {"LOP Days", String.valueOf(record.getLopDays() != null ? record.getLopDays() : 0)},
                        {"Payment Date", record.getPaymentDate() != null ? record.getPaymentDate().format(DATE_FORMAT) : "Pending"}
                };

                for (int i = 0; i < stats.length; i++) {
                    float statX = margin + i * (statCardWidth + 10);

                    // Card background
                    cs.setNonStrokingColor(COLOR_WHITE);
                    cs.addRect(statX, yPos - statCardHeight, statCardWidth, statCardHeight);
                    cs.fill();

                    // TOP COLORED BAR for each card
                    cs.setNonStrokingColor(statColors[i]);
                    cs.addRect(statX, yPos - 6, statCardWidth, 6);
                    cs.fill();

                    // Card border
                    cs.setStrokingColor(COLOR_BORDER);
                    cs.setLineWidth(0.5f);
                    cs.addRect(statX, yPos - statCardHeight, statCardWidth, statCardHeight);
                    cs.stroke();

                    // Label
                    cs.setNonStrokingColor(COLOR_GRAY);
                    cs.setFont(fontRegular, 8);
                    cs.beginText();
                    cs.newLineAtOffset(statX + 8, yPos - 22);
                    cs.showText(stats[i][0]);
                    cs.endText();

                    // Value - COLORED
                    cs.setNonStrokingColor(statColors[i]);
                    cs.setFont(fontBold, 16);
                    float valWidth = getTextWidth(stats[i][1], fontBold, 16);
                    cs.beginText();
                    cs.newLineAtOffset(statX + (statCardWidth - valWidth)/2, yPos - 45);
                    cs.showText(stats[i][1]);
                    cs.endText();
                }

                // ========== SALARY TABLE ==========
                yPos = yPos - statCardHeight - 35;

                // Section Title
                cs.setNonStrokingColor(COLOR_DARK);
                cs.setFont(fontBold, 13);
                cs.beginText();
                cs.newLineAtOffset(margin, yPos);
                cs.showText("Salary Breakdown");
                cs.endText();

                yPos -= 20;
                float tableTopY = yPos;

                float col1Width = contentWidth * 0.40f;
                float col2Width = contentWidth * 0.10f;
                float col3Width = contentWidth * 0.40f;
                float col4Width = contentWidth * 0.10f;

                float col1X = margin;
                float col2X = col1X + col1Width;
                float col3X = col2X + col2Width;
                float col4X = col3X + col3Width;

                // Table Header - SIDEBAR COLOR
                cs.setNonStrokingColor(COLOR_PRIMARY);
                cs.addRect(margin, tableTopY - 22, contentWidth, 26);
                cs.fill();

                cs.setNonStrokingColor(COLOR_WHITE);
                cs.setFont(fontBold, 9);

                cs.beginText();
                cs.newLineAtOffset(col1X + 8, tableTopY - 10);
                cs.showText("EARNINGS");
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(col2X + 8, tableTopY - 10);
                cs.showText("AMOUNT");
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(col3X + 8, tableTopY - 10);
                cs.showText("DEDUCTIONS");
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(col4X + 8, tableTopY - 10);
                cs.showText("AMOUNT");
                cs.endText();

                // Table Rows
                float rowY = tableTopY - 38;
                float rowHeight = 20;

                String[][] earnings = {
                        {"Basic Salary", formatAmount(record.getBasicSalary())},
                        {"House Rent Allowance (HRA)", formatAmount(record.getHra())},
                        {"Travel Allowance", formatAmount(record.getTravelAllow())},
                        {"Medical Allowance", formatAmount(record.getMedicalAllow())},
                        {"Special Allowance", formatAmount(record.getSpecialAllow())}
                };

                String[][] deductions = {
                        {"Provident Fund (PF)", formatAmount(record.getProvidentFund())},
                        {"Professional Tax", formatAmount(record.getProfessionalTax())},
                        {"Income Tax (TDS)", formatAmount(record.getIncomeTax())},
                        {"Loan Deduction", formatAmount(record.getLoanDeduction())},
                        {"Other Deductions", formatAmount(record.getOtherDeductions())}
                };

                int rowCount = Math.max(earnings.length, deductions.length);

                for (int i = 0; i < rowCount; i++) {
                    float currentRowY = rowY - (i * rowHeight);

                    // Alternating row colors
                    if (i % 2 == 0) {
                        cs.setNonStrokingColor(COLOR_LIGHT_GRAY);
                    } else {
                        cs.setNonStrokingColor(COLOR_WHITE);
                    }
                    cs.addRect(margin, currentRowY - 14, contentWidth, rowHeight);
                    cs.fill();

                    cs.setNonStrokingColor(COLOR_DARK);
                    cs.setFont(fontRegular, 9);

                    if (i < earnings.length) {
                        cs.beginText();
                        cs.newLineAtOffset(col1X + 8, currentRowY - 5);
                        cs.showText(earnings[i][0]);
                        cs.endText();

                        // Earnings amounts - GREEN
                        cs.setNonStrokingColor(COLOR_GREEN);
                        cs.beginText();
                        cs.newLineAtOffset(col2X + 8, currentRowY - 5);
                        cs.showText(earnings[i][1]);
                        cs.endText();
                        cs.setNonStrokingColor(COLOR_DARK);
                    }

                    if (i < deductions.length) {
                        cs.beginText();
                        cs.newLineAtOffset(col3X + 8, currentRowY - 5);
                        cs.showText(deductions[i][0]);
                        cs.endText();

                        // Deductions amounts - RED
                        cs.setNonStrokingColor(COLOR_RED);
                        cs.beginText();
                        cs.newLineAtOffset(col4X + 8, currentRowY - 5);
                        cs.showText(deductions[i][1]);
                        cs.endText();
                        cs.setNonStrokingColor(COLOR_DARK);
                    }
                }

                // Totals Row
                float totalY = rowY - (rowCount * rowHeight) - 10;

                cs.setStrokingColor(COLOR_BORDER);
                cs.setLineWidth(1);
                cs.moveTo(margin, totalY + 8);
                cs.lineTo(pageWidth - margin, totalY + 8);
                cs.stroke();

                // Gross Earnings - GREEN
                cs.setNonStrokingColor(COLOR_GREEN);
                cs.setFont(fontBold, 10);
                cs.beginText();
                cs.newLineAtOffset(col1X + 8, totalY - 5);
                cs.showText("GROSS EARNINGS");
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(col2X + 8, totalY - 5);
                cs.showText(formatAmount(record.getGrossEarnings()));
                cs.endText();

                // Total Deductions - RED
                cs.setNonStrokingColor(COLOR_RED);
                cs.beginText();
                cs.newLineAtOffset(col3X + 8, totalY - 5);
                cs.showText("TOTAL DEDUCTIONS");
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(col4X + 8, totalY - 5);
                cs.showText(formatAmount(record.getTotalDeductions()));
                cs.endText();

                // ========== NET SALARY BOX WITH SIDEBAR COLORS ==========
                float netY = totalY - 45;
                float netBoxHeight = 55;

                // Background - SIDEBAR LIGHT
                cs.setNonStrokingColor(COLOR_PRIMARY_LIGHT);
                cs.addRect(margin, netY - netBoxHeight, contentWidth, netBoxHeight);
                cs.fill();

                // Border - SIDEBAR PRIMARY
                cs.setStrokingColor(COLOR_PRIMARY);
                cs.setLineWidth(1.5f);
                cs.addRect(margin, netY - netBoxHeight, contentWidth, netBoxHeight);
                cs.stroke();

                // Label - SIDEBAR PRIMARY
                cs.setNonStrokingColor(COLOR_PRIMARY);
                cs.setFont(fontBold, 12);
                cs.beginText();
                cs.newLineAtOffset(margin + 15, netY - 20);
                cs.showText("NET TAKE-HOME SALARY");
                cs.endText();

                // Amount - SIDEBAR VIOLET
                String netAmount = "Rs. " + formatAmountPlain(record.getNetSalary());
                cs.setFont(fontBold, 24);
                cs.setNonStrokingColor(COLOR_VIOLET);
                float netWidth = getTextWidth(netAmount, fontBold, 24);
                cs.beginText();
                cs.newLineAtOffset(pageWidth - margin - netWidth - 15, netY - 24);
                cs.showText(netAmount);
                cs.endText();

                // ========== FOOTER ==========
                cs.setNonStrokingColor(COLOR_GRAY);
                cs.setFont(fontRegular, 8);

                String footer = "This is a computer-generated payslip and does not require a signature.  |  Generated on " +
                        java.time.LocalDate.now().format(DATE_FORMAT);
                float footerWidth = getTextWidth(footer, fontRegular, 8);
                cs.beginText();
                cs.newLineAtOffset((pageWidth - footerWidth) / 2, 35);
                cs.showText(footer);
                cs.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private void drawCircle(PDPageContentStream cs, float centerX, float centerY, float radius) throws Exception {
        float k = 0.5522847498f;
        float dx = radius * k;
        float dy = radius * k;

        cs.moveTo(centerX - radius, centerY);
        cs.curveTo(centerX - radius, centerY + dy, centerX - dx, centerY + radius, centerX, centerY + radius);
        cs.curveTo(centerX + dx, centerY + radius, centerX + radius, centerY + dy, centerX + radius, centerY);
        cs.curveTo(centerX + radius, centerY - dy, centerX + dx, centerY - radius, centerX, centerY - radius);
        cs.curveTo(centerX - dx, centerY - radius, centerX - radius, centerY - dy, centerX - radius, centerY);
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    private String formatAmount(Double amount) {
        if (amount == null) amount = 0.0;
        return String.format("%,.0f", amount);
    }

    private String formatAmountPlain(Double amount) {
        return formatAmount(amount);
    }

    private String cleanString(String str) {
        if (str == null || str.trim().isEmpty()) return "—";
        return str;
    }

    private Color getStatusColor(String status) {
        if (status == null) return COLOR_GRAY;
        switch (status.toUpperCase()) {
            case "DRAFT": return new Color(100, 116, 139);
            case "PENDING": return COLOR_AMBER;
            case "APPROVED": return new Color(59, 130, 246);
            case "PROCESSED": return COLOR_GREEN;
            case "PAID": return COLOR_VIOLET;
            default: return COLOR_GRAY;
        }
    }

    private float getTextWidth(String text, PDFont font, float fontSize) throws Exception {
        return font.getStringWidth(text) / 1000 * fontSize;
    }
}