package com.graphy.lms.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PDFReceiptService {

    private static final Logger logger = LoggerFactory.getLogger(PDFReceiptService.class);
    private static final String RECEIPTS_DIR = "receipts/";

    /**
     * Generate PDF receipt and return as byte array
     */
    public byte[] generateReceiptPDF(String receiptNumber, String studentName, 
                                     Long userId, BigDecimal amount, String currency,
                                     String paymentMode, String transactionRef,
                                     LocalDateTime paymentDate) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Set margins
            document.setMargins(40, 40, 40, 40);

            // Header
            addHeader(document, receiptNumber);

            // Receipt Details
            addReceiptDetails(document, receiptNumber, paymentDate);

            // Student Information
            addStudentInformation(document, studentName, userId);

            // Payment Details Table
            addPaymentDetailsTable(document, amount, currency, paymentMode, transactionRef);

            // Amount in Words
            addAmountInWords(document, amount, currency);

            // Footer
            addFooter(document);

            document.close();

            logger.info("PDF receipt generated successfully: {}", receiptNumber);
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Failed to generate PDF receipt: {}", e.getMessage());
            throw new RuntimeException("Failed to generate PDF receipt: " + e.getMessage());
        }
    }

    /**
     * Save PDF receipt to file system
     */
    public String saveReceiptToFile(byte[] pdfBytes, String receiptNumber) {
        try {
            // Create directory if not exists
            File directory = new File(RECEIPTS_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = RECEIPTS_DIR + receiptNumber + ".pdf";
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(pdfBytes);
            }

            logger.info("PDF receipt saved to file: {}", filePath);
            return filePath;

        } catch (IOException e) {
            logger.error("Failed to save PDF receipt to file: {}", e.getMessage());
            throw new RuntimeException("Failed to save PDF receipt: " + e.getMessage());
        }
    }

    private void addHeader(Document document, String receiptNumber) {
        // Institute Name
        Paragraph instituteName = new Paragraph("LMS EDUCATIONAL INSTITUTE")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(0, 51, 102));
        document.add(instituteName);

        // Subtitle
        Paragraph subtitle = new Paragraph("Fee Payment Receipt")
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitle);

        // Horizontal line
        document.add(new Paragraph()
                .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 2))
                .setMarginBottom(20));
    }

    private void addReceiptDetails(Document document, String receiptNumber, LocalDateTime paymentDate) {
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));

        // Receipt Number
        table.addCell(createCell("Receipt Number:", true));
        table.addCell(createCell(receiptNumber, false));

        // Date & Time
        table.addCell(createCell("Date & Time:", true));
        table.addCell(createCell(paymentDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")), false));

        document.add(table);
        document.add(new Paragraph().setMarginBottom(15));
    }

    private void addStudentInformation(Document document, String studentName, Long userId) {
        document.add(new Paragraph("Student Information")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(10));

        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(createCell("Student Name:", true));
        table.addCell(createCell(studentName, false));

        table.addCell(createCell("Student ID:", true));
        table.addCell(createCell(String.valueOf(userId), false));

        document.add(table);
        document.add(new Paragraph().setMarginBottom(15));
    }

    private void addPaymentDetailsTable(Document document, BigDecimal amount, 
                                       String currency, String paymentMode, 
                                       String transactionRef) {
        document.add(new Paragraph("Payment Details")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(10));

        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(createCell("Amount Paid:", true));
        table.addCell(createCell(currency + " " + amount, false));

        table.addCell(createCell("Payment Mode:", true));
        table.addCell(createCell(paymentMode, false));

        table.addCell(createCell("Transaction Reference:", true));
        table.addCell(createCell(transactionRef != null ? transactionRef : "N/A", false));

        table.addCell(createCell("Payment Status:", true));
        Cell statusCell = createCell("SUCCESS", false);
        statusCell.setFontColor(ColorConstants.GREEN).setBold();
        table.addCell(statusCell);

        document.add(table);
        document.add(new Paragraph().setMarginBottom(15));
    }

    private void addAmountInWords(Document document, BigDecimal amount, String currency) {
        String amountInWords = convertAmountToWords(amount);
        
        Paragraph amountWords = new Paragraph("Amount in Words: " + amountInWords + " " + currency + " Only")
                .setItalic()
                .setFontSize(10)
                .setMarginBottom(20);
        document.add(amountWords);
    }

    private void addFooter(Document document) {
        document.add(new Paragraph()
                .setBorderTop(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                .setMarginTop(30));

        Paragraph footer = new Paragraph("This is a computer-generated receipt and does not require a signature.")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10);
        document.add(footer);

        Paragraph contact = new Paragraph("For queries, contact: accounts@lms.edu | +91-123-456-7890")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY);
        document.add(contact);
    }

    private Cell createCell(String content, boolean isBold) {
        Cell cell = new Cell().add(new Paragraph(content));
        if (isBold) {
            cell.setBold();
        }
        cell.setBorder(null);
        cell.setPadding(5);
        return cell;
    }

    /**
     * Convert amount to words (simplified version)
     */
    private String convertAmountToWords(BigDecimal amount) {
        // Simplified implementation - in production, use a library or comprehensive logic
        long intPart = amount.longValue();
        
        if (intPart == 0) {
            return "Zero";
        }
        
        String[] ones = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
        String[] teens = {"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        
        // This is a basic implementation for amounts up to 99,999
        // For production, use a comprehensive number-to-words library
        
        if (intPart < 10) {
            return ones[(int) intPart];
        } else if (intPart < 20) {
            return teens[(int) (intPart - 10)];
        } else if (intPart < 100) {
            return tens[(int) (intPart / 10)] + " " + ones[(int) (intPart % 10)];
        } else if (intPart < 1000) {
            return ones[(int) (intPart / 100)] + " Hundred " + convertAmountToWords(BigDecimal.valueOf(intPart % 100));
        } else if (intPart < 100000) {
            return convertAmountToWords(BigDecimal.valueOf(intPart / 1000)) + " Thousand " + 
                   convertAmountToWords(BigDecimal.valueOf(intPart % 1000));
        }
        
        return String.valueOf(intPart); // Fallback
    }
}