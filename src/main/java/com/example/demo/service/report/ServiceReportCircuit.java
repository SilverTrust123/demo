package com.example.demo.service.report;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.db.entity.Circuit;
import com.example.demo.db.repository.CircuitRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.*;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;

@Service
public class ServiceReportCircuit {
    @Autowired
    private CircuitRepository circuitRepository;

    public byte[] generateCircuitExcelReport() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            List<Circuit> cirs = circuitRepository.findAll();
            Sheet sheet = workbook.createSheet("CircuitExcelReport");

            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            String[] columns = { "Device ID", "Temperature", "Humidity" };

            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }
            int rowIdx = 1;
            for (Circuit cir : cirs) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(cir.getDeviceId());
                row.createCell(1).setCellValue(cir.getCurrent());
                row.createCell(2).setCellValue(cir.getEnergy());
                row.createCell(3).setCellValue(cir.getPower());
                row.createCell(4).setCellValue(cir.getVoltage());
                row.createCell(5).setCellValue(cir.getTimestamp());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Excel 報表生成失敗: " + e.getMessage());
        }
    }

    public byte[] generateCircuitExcelReportBetweenTimes(int start, int end) {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            List<Circuit> cirs = circuitRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
            Sheet sheet = workbook.createSheet("CircuitExcelReportBetweenTimes");

            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            String[] columns = { "Device ID", "Temperature", "Humidity" };

            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }
            int rowIdx = 1;
            for (Circuit cir : cirs) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(cir.getDeviceId());
                row.createCell(1).setCellValue(cir.getCurrent());
                row.createCell(2).setCellValue(cir.getEnergy());
                row.createCell(3).setCellValue(cir.getPower());
                row.createCell(4).setCellValue(cir.getVoltage());
                row.createCell(5).setCellValue(cir.getTimestamp());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Excel 報表生成失敗: " + e.getMessage());
        }
    }

    // pdf
    public byte[] generateTemparatureAndHumidityReport() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<Circuit> cirs = circuitRepository.findAll();

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("All Circuit Data Report", fontTitle));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.addCell("Device ID");
            table.addCell("voltage");
            table.addCell("current");
            table.addCell("power");
            table.addCell("energy");
            table.addCell("timestamp");

            for (Circuit cir : cirs) {
                table.addCell(String.valueOf(cir.getDeviceId()));
                table.addCell(String.valueOf(cir.getVoltage()));
                table.addCell(String.valueOf(cir.getCurrent()));
                table.addCell(String.valueOf(cir.getPower()));
                table.addCell(String.valueOf(cir.getEnergy()));
                table.addCell(String.valueOf(cir.getTimestamp()));
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF general fail: " + e.getMessage());
        }
    }

    public byte[] generateTemparatureAndHumidityReportBeweenTimes(int start, int end) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<Circuit> cirs = circuitRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("All Circuit Data Report Between Times", fontTitle));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.addCell("Device ID");
            table.addCell("voltage");
            table.addCell("current");
            table.addCell("power");
            table.addCell("energy");
            table.addCell("timestamp");

            for (Circuit cir : cirs) {
                table.addCell(String.valueOf(cir.getDeviceId()));
                table.addCell(String.valueOf(cir.getVoltage()));
                table.addCell(String.valueOf(cir.getCurrent()));
                table.addCell(String.valueOf(cir.getPower()));
                table.addCell(String.valueOf(cir.getEnergy()));
                table.addCell(String.valueOf(cir.getTimestamp()));
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF general fail: " + e.getMessage());
        }
    }
}