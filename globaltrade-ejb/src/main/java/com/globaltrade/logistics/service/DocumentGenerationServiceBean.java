package com.globaltrade.logistics.service;

import com.globaltrade.logistics.entity.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import jakarta.ejb.Stateless;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class DocumentGenerationServiceBean {

    public byte[] generateVendorScorecard(Vendor vendor, List<PurchaseOrder> pos) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            document.add(new Paragraph("VENDOR SCORECARD").setBold().setFontSize(18));
            document.add(new Paragraph("Vendor: " + vendor.getCompanyName()));
            document.add(new Paragraph("Performance Score: " + (vendor.getPerformanceScore() != null ? vendor.getPerformanceScore() : 100)));
            document.add(new Paragraph("Defect Rate: " + (vendor.getDefectRate() != null ? (vendor.getDefectRate() * 100) : 0) + "%"));
            document.add(new Paragraph("On-Time Delivery: " + (vendor.getOnTimeDeliveryRate() != null ? (vendor.getOnTimeDeliveryRate() * 100) : 100) + "%"));
            document.add(new Paragraph("\nRecent Orders:"));
            
            for (PurchaseOrder po : pos) {
                document.add(new Paragraph("PO-" + po.getId() + " | SKU: " + po.getSku() + " | Status: " + po.getStatus()));
            }
            
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
    
    public byte[] generateCommercialInvoice(AdvancedShippingNotice asn) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            document.add(new Paragraph("COMMERCIAL INVOICE").setBold().setFontSize(18));
            document.add(new Paragraph("ASN ID: " + asn.getId()));
            document.add(new Paragraph("Vendor: " + asn.getPurchaseOrder().getVendor().getCompanyName()));
            document.add(new Paragraph("Dimensions: " + asn.getDimensions()));
            document.add(new Paragraph("Weight: " + asn.getWeight() + " kg"));
            document.add(new Paragraph("Pallet Count: " + asn.getPalletCount()));
            
            document.add(new Paragraph("\nSHIP-TO DETAILS:").setBold());
            document.add(new Paragraph("Name: " + asn.getReceiverName()));
            document.add(new Paragraph("Email: " + asn.getReceiverEmail()));
            document.add(new Paragraph("Mobile: " + asn.getReceiverMobile()));
            document.add(new Paragraph("Address: " + asn.getReceiverAddress()));
            
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
    
    public byte[] generatePackingList(AdvancedShippingNotice asn) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            document.add(new Paragraph("PACKING LIST").setBold().setFontSize(18));
            document.add(new Paragraph("ASN ID: " + asn.getId()));
            document.add(new Paragraph("PO ID: " + asn.getPurchaseOrder().getId()));
            document.add(new Paragraph("Vendor: " + asn.getPurchaseOrder().getVendor().getCompanyName()));
            document.add(new Paragraph("\nPackage Details:"));
            document.add(new Paragraph("- Pallet Count: " + asn.getPalletCount()));
            document.add(new Paragraph("- Total Weight: " + asn.getWeight() + " kg"));
            document.add(new Paragraph("- Dimensions: " + asn.getDimensions()));
            
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
    
    public byte[] generatePaymentSettlementReport(List<PaymentSettlement> settlements) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            document.add(new Paragraph("PAYMENT SETTLEMENT STATEMENT").setBold().setFontSize(18));
            document.add(new Paragraph("Generated Date: " + LocalDateTime.now()));
            document.add(new Paragraph("\n"));
            
            for (PaymentSettlement s : settlements) {
                String status = s.getIsPaid() ? "PAID" : "PENDING";
                document.add(new Paragraph("Settlement #" + s.getId() + " | PO: " + (s.getPurchaseOrder() != null ? s.getPurchaseOrder().getId() : "N/A") + " | Amount: $" + s.getAmount() + " | Status: " + status));
            }
            
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Note: Invoices marked as PENDING are scheduled for payment in the next settlement cycle (Net-30).").setFontSize(10).setItalic());
            
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public byte[] generateTaxReceipt(Shipment shipment) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            document.add(new Paragraph("FINAL TAX INVOICE / RECEIPT").setBold().setFontSize(18));
            document.add(new Paragraph("Tracking Number: " + shipment.getTrackingNumber()));
            document.add(new Paragraph("Delivery Address: " + shipment.getDeliveryAddress()));
            document.add(new Paragraph("Customer ID: " + shipment.getCustomerId()));
            document.add(new Paragraph("Status: " + shipment.getStatus()));
            
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public byte[] generateCustomerSpendHistory(List<Shipment> shipments) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Spend History");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Tracking Number");
            headerRow.createCell(1).setCellValue("Status");
            headerRow.createCell(2).setCellValue("Destination");
            headerRow.createCell(3).setCellValue("Delivery Address");
            headerRow.createCell(4).setCellValue("Amount");

            int rowNum = 1;
            for (Shipment s : shipments) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(s.getTrackingNumber());
                row.createCell(1).setCellValue(s.getStatus().toString());
                row.createCell(2).setCellValue(s.getDestination() != null ? s.getDestination() : "N/A");
                row.createCell(3).setCellValue(s.getDeliveryAddress() != null ? s.getDeliveryAddress() : "N/A");
                row.createCell(4).setCellValue(1500.00); // Mock value since we don't have a spend column on Shipment
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}