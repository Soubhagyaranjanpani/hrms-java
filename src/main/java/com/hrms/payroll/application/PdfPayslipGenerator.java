// File: com/hrms/payroll/application/PdfPayslipGenerator.java
package com.hrms.payroll.application;

import com.hrms.payroll.dto.PayrollRecordResponse;
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

@Service
public class PdfPayslipGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

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

                float leftColX = margin;
                float leftAmtX = margin + contentWidth * 0.42f;
                float rightColX = margin + contentWidth * 0.55f;
                float rightAmtX = pageWidth - margin - 60;

                // ========== HEADER ==========
                float currentY = pageHeight - 40;

                // Logo dimensions (adjusted for your logo)
                float logoWidth = 160;
                float logoHeight = 50;

                // Load and draw logo with proper color handling
                boolean logoDrawn = false;

                // Try multiple paths and formats
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
                                // Convert to PNG bytes preserving colors
                                ByteArrayOutputStream pngBytes = new ByteArrayOutputStream();
                                ImageIO.write(bufferedImage, "png", pngBytes);
                                PDImageXObject logo = PDImageXObject.createFromByteArray(
                                        document, pngBytes.toByteArray(), "logo");

                                // Calculate height based on aspect ratio to maintain proportions
                                float aspectRatio = (float) bufferedImage.getWidth() / bufferedImage.getHeight();
                                logoHeight = logoWidth / aspectRatio;

                                // Draw logo at top-left with original colors
                                cs.drawImage(logo, margin, currentY - logoHeight, logoWidth, logoHeight);
                                logoDrawn = true;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to load logo from " + logoPath + ": " + e.getMessage());
                    }
                }

                // Fallback: Try to load from WEBP format directly
                if (!logoDrawn) {
                    try {
                        InputStream webpStream = getClass().getResourceAsStream("/images/arigen-logo-23.webp");
                        if (webpStream != null) {
                            BufferedImage bufferedImage = ImageIO.read(webpStream);
                            if (bufferedImage != null) {
                                ByteArrayOutputStream pngBytes = new ByteArrayOutputStream();
                                ImageIO.write(bufferedImage, "png", pngBytes);
                                PDImageXObject logo = PDImageXObject.createFromByteArray(
                                        document, pngBytes.toByteArray(), "logo");

                                float aspectRatio = (float) bufferedImage.getWidth() / bufferedImage.getHeight();
                                logoHeight = logoWidth / aspectRatio;
                                cs.drawImage(logo, margin, currentY - logoHeight, logoWidth, logoHeight);
                                logoDrawn = true;
                            }
                            webpStream.close();
                        }
                    } catch (Exception e) {
                        System.err.println("WEBP fallback failed: " + e.getMessage());
                    }
                }

                // Company name (with proper spacing based on logo presence)
                float companyNameX = margin + logoWidth + 15;
                if (!logoDrawn) {
                    companyNameX = margin;
                }

                // ARIGEN TECHNOLOGY - in your original colors (blue/dark theme)
//                cs.setNonStrokingColor(new java.awt.Color(0, 51, 102)); // Deep blue for the brand
//                cs.setFont(fontBold, 18);
//                cs.beginText();
//                cs.newLineAtOffset(companyNameX, currentY - 8);
//                cs.showText("ARIGEN TECHNOLOGY");
//                cs.endText();

                // Tagline - in grey as per your logo
//                cs.setNonStrokingColor(new java.awt.Color(102, 102, 102));
//                cs.setFont(fontRegular, 8);
//                cs.beginText();
//                cs.newLineAtOffset(companyNameX, currentY - 22);
//                cs.showText("SOLUTION FOR GENERATION NEXT");
//                cs.endText();

                // PAYSLIP title on right
                cs.setNonStrokingColor(new java.awt.Color(0, 0, 0));
                cs.setFont(fontBold, 16);
                String title = "PAYSLIP";
                float titleWidth = getTextWidth(title, fontBold, 16);
                cs.beginText();
                cs.newLineAtOffset(pageWidth - margin - titleWidth, currentY - 8);
                cs.showText(title);
                cs.endText();

                // Month
                cs.setFont(fontRegular, 10);
                String monthText = record.getPayrollMonth() != null ? record.getPayrollMonth() : "";
                float monthWidth = getTextWidth(monthText, fontRegular, 10);
                cs.beginText();
                cs.newLineAtOffset(pageWidth - margin - monthWidth, currentY - 23);
                cs.showText(monthText);
                cs.endText();

                // Divider line with brand color
                currentY -= 38;
                cs.setStrokingColor(new java.awt.Color(0, 51, 102));
                cs.setLineWidth(1.5f);
                cs.moveTo(margin, currentY);
                cs.lineTo(pageWidth - margin, currentY);
                cs.stroke();

                // ========== EMPLOYEE INFORMATION ==========
                currentY -= 20;

                drawInfoRow(cs, fontRegular, fontBold, margin, currentY, "Name", cleanString(record.getEmployee()));
                currentY -= 18;
                drawInfoRow(cs, fontRegular, fontBold, margin, currentY, "Role", cleanString(record.getDesignation()));
                currentY -= 18;
                drawInfoRow(cs, fontRegular, fontBold, margin, currentY, "Date of Joining",
                        record.getDateOfJoining() != null ? record.getDateOfJoining().format(DATE_FORMAT) : "—");
                currentY -= 18;
                drawInfoRow(cs, fontRegular, fontBold, margin, currentY, "Paid Days",
                        String.valueOf(record.getPaidDays() != null ? record.getPaidDays() : 0));
                currentY -= 18;
                drawInfoRow(cs, fontRegular, fontBold, margin, currentY, "Bank A/c", cleanString(record.getBankAccount()));
                currentY -= 18;
                drawInfoRow(cs, fontRegular, fontBold, margin, currentY, "UAN", cleanString(record.getUan()));
                currentY -= 18;
                drawInfoRow(cs, fontRegular, fontBold, margin, currentY, "Employee Code", cleanString(record.getEmployeeCode()));
                currentY -= 18;
                drawInfoRow(cs, fontRegular, fontBold, margin, currentY, "PAN", cleanString(record.getPan()));

                // Divider line
                currentY -= 10;
                cs.setStrokingColor(new java.awt.Color(0, 51, 102));
                cs.setLineWidth(0.8f);
                cs.moveTo(margin, currentY);
                cs.lineTo(pageWidth - margin, currentY);
                cs.stroke();

                // ========== SALARY TABLE ==========
                currentY -= 15;
                float tableStartY = currentY;

                // Earnings & Deductions headers
                cs.setNonStrokingColor(new java.awt.Color(0, 51, 102));
                cs.setFont(fontBold, 11);
                cs.beginText();
                cs.newLineAtOffset(leftColX + 5, tableStartY);
                cs.showText("Earnings");
                cs.endText();
                cs.beginText();
                cs.newLineAtOffset(rightColX + 5, tableStartY);
                cs.showText("Deductions");
                cs.endText();

                // Column headers background
                float headerY = tableStartY - 15;
                cs.setNonStrokingColor(new java.awt.Color(220, 230, 242)); // Light blue background
                cs.addRect(margin, headerY - 12, contentWidth, 18);
                cs.fill();

                // Column headers text
                cs.setNonStrokingColor(new java.awt.Color(0, 51, 102));
                cs.setFont(fontBold, 9);
                cs.beginText();
                cs.newLineAtOffset(leftColX + 5, headerY - 3);
                cs.showText("Particulars");
                cs.endText();
                cs.beginText();
                cs.newLineAtOffset(leftAmtX, headerY - 3);
                cs.showText("Amount");
                cs.endText();
                cs.beginText();
                cs.newLineAtOffset(rightColX + 5, headerY - 3);
                cs.showText("Particulars");
                cs.endText();
                cs.beginText();
                cs.newLineAtOffset(rightAmtX, headerY - 3);
                cs.showText("Amount (Rs.)");
                cs.endText();

                // Vertical divider
                float midX = margin + contentWidth / 2;
                cs.setStrokingColor(new java.awt.Color(180, 180, 180));
                cs.setLineWidth(0.5f);
                cs.moveTo(midX, headerY + 6);
                cs.lineTo(midX, headerY - 255);
                cs.stroke();

                // Table rows
                float rowY = headerY - 28;
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

                    // Alternate row shading
                    if (i % 2 == 0) {
                        cs.setNonStrokingColor(new java.awt.Color(248, 250, 252)); // Very light blue
                        cs.addRect(margin, crY - 11, contentWidth, rowHeight);
                        cs.fill();
                    }

                    cs.setNonStrokingColor(new java.awt.Color(0, 0, 0));
                    cs.setFont(fontRegular, 9);

                    // Earnings column
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

                    // Deductions column
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

                // Gross row
                float grossY = rowY - (maxRows * rowHeight) - 22;

                // Top line
                cs.setStrokingColor(new java.awt.Color(0, 51, 102));
                cs.setLineWidth(1f);
                cs.moveTo(margin, grossY + 5);
                cs.lineTo(pageWidth - margin, grossY + 5);
                cs.stroke();

                // Gross row background
                cs.setNonStrokingColor(new java.awt.Color(240, 245, 250));
                cs.addRect(margin, grossY - 14, contentWidth, 22);
                cs.fill();

                cs.setNonStrokingColor(new java.awt.Color(0, 51, 102));
                cs.setFont(fontBold, 10);
                cs.beginText();
                cs.newLineAtOffset(leftColX + 5, grossY - 4);
                cs.showText("Gross Earnings");
                cs.endText();
                cs.beginText();
                cs.newLineAtOffset(leftAmtX, grossY - 4);
                cs.showText(formatAmount(record.getGrossEarnings()));
                cs.endText();
                cs.beginText();
                cs.newLineAtOffset(rightColX + 5, grossY - 4);
                cs.showText("Gross Deductions");
                cs.endText();
                cs.beginText();
                cs.newLineAtOffset(rightAmtX, grossY - 4);
                cs.showText(formatAmount(record.getTotalDeductions()));
                cs.endText();

                // Bottom line
                cs.moveTo(margin, grossY - 14);
                cs.lineTo(pageWidth - margin, grossY - 14);
                cs.stroke();

                // ========== NET PAYABLE ==========
                float netY = grossY - 50;

                cs.setNonStrokingColor(new java.awt.Color(0, 51, 102));
                cs.setFont(fontBold, 12);
                cs.beginText();
                cs.newLineAtOffset(margin + 5, netY);
                cs.showText("Net Payable Salary:");
                cs.endText();

                cs.setNonStrokingColor(new java.awt.Color(0, 102, 51)); // Green for net amount
                cs.setFont(fontBold, 14);
                String netAmount = "Rs. " + formatAmountPlain(record.getNetSalary());
                cs.beginText();
                cs.newLineAtOffset(leftAmtX, netY);
                cs.showText(netAmount);
                cs.endText();

                // Amount in words
                cs.setNonStrokingColor(new java.awt.Color(80, 80, 80));
                cs.setFont(fontRegular, 9);
                String amountInWords = "Rupees " + convertToWords(record.getNetSalary()) + " Only";
                cs.beginText();
                cs.newLineAtOffset(margin + 5, netY - 16);
                cs.showText(amountInWords);
                cs.endText();

                // ========== FOOTER ==========
                float footerY = 45;

                // Footer line
                cs.setStrokingColor(new java.awt.Color(200, 200, 200));
                cs.setLineWidth(0.5f);
                cs.moveTo(margin, footerY + 5);
                cs.lineTo(pageWidth - margin, footerY + 5);
                cs.stroke();

                // Footer text
                cs.setNonStrokingColor(new java.awt.Color(120, 120, 120));
                cs.setFont(fontRegular, 8);
                String footer = "This is a computer generated payslip, no signature is required.";
                float footerWidth = getTextWidth(footer, fontRegular, 8);
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
        float labelWidth = 110;

        // Label
        cs.setNonStrokingColor(new java.awt.Color(80, 80, 80));
        cs.setFont(fontRegular, 9);
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(label + ":");
        cs.endText();

        // Value
        cs.setNonStrokingColor(new java.awt.Color(0, 0, 0));
        cs.setFont(fontBold, 10);
        cs.beginText();
        cs.newLineAtOffset(x + labelWidth, y);
        cs.showText(value);
        cs.endText();
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