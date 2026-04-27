// File: com/hrms/payroll/application/PdfPayslipGenerator.java
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

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final Color COLOR_DARK = new Color(17, 24, 39);
    private static final Color COLOR_GRAY = new Color(107, 114, 128);
    private static final Color COLOR_LIGHT_GRAY = new Color(249, 250, 251);
    private static final Color COLOR_WHITE = new Color(255, 255, 255);
    private static final Color COLOR_BORDER = new Color(229, 231, 235);
    private static final Color COLOR_HEADER_BG = new Color(243, 244, 246);
    private static final Color COLOR_GREEN = new Color(5, 150, 105);
    private static final Color COLOR_RED = new Color(220, 38, 38);
    private static final Color COLOR_BLUE = new Color(37, 99, 235);
    private static final Color COLOR_BLACK = new Color(0, 0, 0);

    public byte[] generatePayslip(PayrollRecordResponse record) throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                PDFont fontBold = PDType1Font.HELVETICA_BOLD;
                PDFont fontRegular = PDType1Font.HELVETICA;

                float pageWidth = PDRectangle.A4.getWidth();
                float pageHeight = PDRectangle.A4.getHeight();
                float margin = 40;
                float contentWidth = pageWidth - 2 * margin;

                // Column positions for the table
                float leftColX = margin;
                float leftAmtX = margin + contentWidth * 0.42f;
                float rightColX = margin + contentWidth * 0.55f;
                float rightAmtX = pageWidth - margin - 60;

                // ========== TOP BAR ==========
                cs.setNonStrokingColor(COLOR_BLUE);
                cs.addRect(0, pageHeight - 4, pageWidth, 4);
                cs.fill();

                // ========== HEADER ==========
                float yPos = pageHeight - 35;

                cs.setNonStrokingColor(COLOR_BLUE);
                cs.setFont(fontBold, 18);
                cs.beginText();
                cs.newLineAtOffset(margin, yPos);
                cs.showText("HRMS");
                cs.endText();

                cs.setNonStrokingColor(COLOR_BLACK);
                cs.setFont(fontBold, 16);
                String title = "PAYSLIP";
                float titleWidth = getTextWidth(title, fontBold, 16);
                cs.beginText();
                cs.newLineAtOffset(pageWidth - margin - titleWidth, yPos);
                cs.showText(title);
                cs.endText();

                cs.setNonStrokingColor(COLOR_GRAY);
                cs.setFont(fontRegular, 10);
                String monthText = record.getPayrollMonth() != null ? record.getPayrollMonth() : "";
                float monthWidth = getTextWidth(monthText, fontRegular, 10);
                cs.beginText();
                cs.newLineAtOffset(pageWidth - margin - monthWidth, yPos - 14);
                cs.showText(monthText);
                cs.endText();

                // Divider
                yPos -= 30;
                drawLine(cs, margin, yPos, pageWidth - margin, yPos, COLOR_BORDER, 0.5f);

                // ========== EMPLOYEE INFO - SINGLE SIDED LAYOUT ==========
                yPos -= 20;

                drawInfoRow(cs, fontRegular, fontBold, margin, yPos, "Name", cleanString(record.getEmployee()));
                yPos -= 16;
                drawInfoRow(cs, fontRegular, fontBold, margin, yPos, "Designation", cleanString(record.getDesignation()));
                yPos -= 16;
                drawInfoRow(cs, fontRegular, fontBold, margin, yPos, "Date of Joining", record.getDateOfJoining() != null ? record.getDateOfJoining().format(DATE_FORMAT) : "—");
                yPos -= 16;
                drawInfoRow(cs, fontRegular, fontBold, margin, yPos, "Paid Days", String.valueOf(record.getPaidDays() != null ? record.getPaidDays() : 0));
                yPos -= 16;
                drawInfoRow(cs, fontRegular, fontBold, margin, yPos, "Bank A/c", cleanString(record.getBankAccount()));
                yPos -= 16;
                drawInfoRow(cs, fontRegular, fontBold, margin, yPos, "UAN", cleanString(record.getUan()));
                yPos -= 16;
                drawInfoRow(cs, fontRegular, fontBold, margin, yPos, "Employee Code", cleanString(record.getEmployeeCode()));
                yPos -= 16;
                drawInfoRow(cs, fontRegular, fontBold, margin, yPos, "PAN", cleanString(record.getPan()));

                // Divider
                yPos -= 10;
                drawLine(cs, margin, yPos, pageWidth - margin, yPos, COLOR_BORDER, 0.5f);

                // ========== SALARY TABLE ==========
                yPos -= 15;
                float tableTopY = yPos;

                // Section titles
                cs.setNonStrokingColor(COLOR_BLACK);
                cs.setFont(fontBold, 11);
                cs.beginText();
                cs.newLineAtOffset(leftColX + 5, tableTopY);
                cs.showText("Earnings");
                cs.endText();
                cs.beginText();
                cs.newLineAtOffset(rightColX + 5, tableTopY);
                cs.showText("Deductions");
                cs.endText();

                // Table header
                float tY = tableTopY - 14;
                cs.setNonStrokingColor(COLOR_HEADER_BG);
                cs.addRect(margin, tY - 14, contentWidth, 20);
                cs.fill();

                cs.setNonStrokingColor(COLOR_BLACK);
                cs.setFont(fontBold, 9);

                // Left headers
                cs.beginText();
                cs.newLineAtOffset(leftColX + 5, tY - 4);
                cs.showText("Particulars");
                cs.endText();
                cs.beginText();
                cs.newLineAtOffset(leftAmtX, tY - 4);
                cs.showText("Amount (Rs.)");
                cs.endText();

                // Right headers
                cs.beginText();
                cs.newLineAtOffset(rightColX + 5, tY - 4);
                cs.showText("Particulars");
                cs.endText();
                cs.beginText();
                cs.newLineAtOffset(rightAmtX, tY - 4);
                cs.showText("Amount (Rs.)");
                cs.endText();

                // Vertical divider between left and right
                float midX = margin + contentWidth / 2;
                cs.setStrokingColor(COLOR_BORDER);
                cs.setLineWidth(0.3f);
                cs.moveTo(midX, tY - 14);
                cs.lineTo(midX, tY - 500);
                cs.stroke();

                // Rows
                float rowY = tY - 30;
                float rowHeight = 17;

                String[][] earnings = {
                        {"Basic", formatAmount(record.getBasicSalary())},
                        {"HRA", formatAmount(record.getHra())},
                        {"Conveyance", formatAmount(record.getTravelAllow())},
                        {"Special Allowance", formatAmount(record.getSpecialAllow())},
                        {"Bonus", formatAmount(record.getBonusAmount())},
                        {"Overtime Payout", formatAmount(record.getOvertimePayout())},
                        {"Arrears / Advance", formatAmount(record.getArrears())},
                        {"Leave Encashment", formatAmount(record.getLeaveEncashment())},
                        {"Notice Period", formatAmount(record.getNoticePeriodPay())},
                        {"Medical", formatAmount(record.getMedicalAllow())},
                        {"Others", formatAmount(record.getOtherEarnings())},
                        {"Leave Travel Allowance", formatAmount(record.getLeaveTravelAllowance())},
                        {"Telephone (Residence)", formatAmount(record.getTelephoneAllowance())}
                };

                String[][] deductions = {
                        {"Provident Fund", formatAmount(record.getProvidentFund())},
                        {"TDS", formatAmount(record.getIncomeTax())},
                        {"ESIC EE", formatAmount(record.getEsiEmployee())},
                        {"Advance", formatAmount(record.getLoanDeduction())},
                        {"Professional Tax", formatAmount(record.getProfessionalTax())}
                };

                int maxRows = Math.max(earnings.length, deductions.length);

                for (int i = 0; i < maxRows; i++) {
                    float crY = rowY - (i * rowHeight);

                    // Row background
                    if (i % 2 == 0) {
                        cs.setNonStrokingColor(COLOR_LIGHT_GRAY);
                        cs.addRect(margin, crY - 12, contentWidth, rowHeight);
                        cs.fill();
                    }

                    cs.setNonStrokingColor(COLOR_BLACK);
                    cs.setFont(fontRegular, 9);

                    // Left (Earnings)
                    if (i < earnings.length) {
                        cs.beginText();
                        cs.newLineAtOffset(leftColX + 5, crY - 3);
                        cs.showText(earnings[i][0]);
                        cs.endText();
                        cs.beginText();
                        cs.newLineAtOffset(leftAmtX, crY - 3);
                        cs.showText(earnings[i][1]);
                        cs.endText();
                    }

                    // Right (Deductions)
                    if (i < deductions.length) {
                        cs.beginText();
                        cs.newLineAtOffset(rightColX + 5, crY - 3);
                        cs.showText(deductions[i][0]);
                        cs.endText();
                        cs.beginText();
                        cs.newLineAtOffset(rightAmtX, crY - 3);
                        cs.showText(deductions[i][1]);
                        cs.endText();
                    }
                }

                // Gross / Total row
                float grossY = rowY - (maxRows * rowHeight) - 15;
                drawLine(cs, margin, grossY + 5, pageWidth - margin, grossY + 5, COLOR_BORDER, 0.5f);

                cs.setNonStrokingColor(COLOR_HEADER_BG);
                cs.addRect(margin, grossY - 12, contentWidth, 20);
                cs.fill();

                cs.setNonStrokingColor(COLOR_BLACK);
                cs.setFont(fontBold, 10);

                cs.beginText();
                cs.newLineAtOffset(leftColX + 5, grossY - 2);
                cs.showText("Gross Earnings");
                cs.endText();
                cs.beginText();
                cs.newLineAtOffset(leftAmtX, grossY - 2);
                cs.showText(formatAmount(record.getGrossEarnings()));
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(rightColX + 5, grossY - 2);
                cs.showText("Gross Deductions");
                cs.endText();
                cs.beginText();
                cs.newLineAtOffset(rightAmtX, grossY - 2);
                cs.showText(formatAmount(record.getTotalDeductions()));
                cs.endText();

                // ========== NET PAYABLE ==========
                float netY = grossY - 45;
                drawLine(cs, margin, netY + 10, pageWidth - margin, netY + 10, COLOR_BORDER, 0.5f);

                cs.setNonStrokingColor(COLOR_BLACK);
                cs.setFont(fontBold, 11);
                cs.beginText();
                cs.newLineAtOffset(margin + 5, netY - 5);
                cs.showText("Net Payable Salary:");
                cs.endText();

                cs.setNonStrokingColor(COLOR_GREEN);
                cs.setFont(fontBold, 14);
                String netAmount = "Rs. " + formatAmountPlain(record.getNetSalary());
                cs.beginText();
                cs.newLineAtOffset(leftAmtX, netY - 5);
                cs.showText(netAmount);
                cs.endText();

                // Amount in words
                cs.setNonStrokingColor(COLOR_GRAY);
                cs.setFont(fontRegular, 9);
                String amountInWords = "Rupees " + convertToWords(record.getNetSalary()) + " Only";
                cs.beginText();
                cs.newLineAtOffset(margin + 5, netY - 22);
                cs.showText(amountInWords);
                cs.endText();

                // ========== FOOTER ==========
                cs.setNonStrokingColor(COLOR_GRAY);
                cs.setFont(fontRegular, 7);
                String footer = "This is a computer generated payslip no signature is required";
                float footerWidth = getTextWidth(footer, fontRegular, 7);
                cs.beginText();
                cs.newLineAtOffset((pageWidth - footerWidth) / 2, 25);
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
        // Two-column layout for info
        float labelWidth = 120;

        cs.setNonStrokingColor(COLOR_GRAY);
        cs.setFont(fontRegular, 9);
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(label + ":");
        cs.endText();

        cs.setNonStrokingColor(COLOR_BLACK);
        cs.setFont(fontBold, 11);
        cs.beginText();
        cs.newLineAtOffset(x + labelWidth, y);
        cs.showText(value);
        cs.endText();
    }

    private void drawLine(PDPageContentStream cs, float x1, float y1, float x2, float y2, Color color, float width) throws Exception {
        cs.setStrokingColor(color);
        cs.setLineWidth(width);
        cs.moveTo(x1, y1);
        cs.lineTo(x2, y2);
        cs.stroke();
    }

    private String convertToWords(Double amount) {
        if (amount == null || amount == 0) return "Zero";
        long num = Math.round(amount);
        return numberToWords(num);
    }

    private String numberToWords(long n) {
        if (n == 0) return "Zero";
        String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
        String[] teens = {"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
        int[] divisors = {10000000, 100000, 1000, 1};
        String[] labels = {"Crore", "Lakh", "Thousand", ""};

        StringBuilder words = new StringBuilder();
        for (int i = 0; i < divisors.length; i++) {
            int chunk = (int) (n / divisors[i]) % (i == 0 ? 100 : 100);
            if (chunk > 0) {
                words.append(convertChunk(chunk, units, teens, tens)).append(" ").append(labels[i]).append(" ");
            }
        }
        return words.toString().trim();
    }

    private String convertChunk(int n, String[] units, String[] teens, String[] tens) {
        StringBuilder sb = new StringBuilder();
        if (n >= 100) { sb.append(units[n / 100]).append(" Hundred "); n %= 100; }
        if (n >= 20) { sb.append(tens[n / 10]).append(" "); n %= 10; }
        if (n >= 10) { sb.append(teens[n - 10]); n = 0; }
        if (n > 0) { sb.append(units[n]); }
        return sb.toString().trim();
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

    private float getTextWidth(String text, PDFont font, float fontSize) throws Exception {
        return font.getStringWidth(text) / 1000 * fontSize;
    }
}