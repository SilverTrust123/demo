package com.example.demo.service.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.db.entity.AirQuality;
import com.example.demo.db.repository.AirQualityRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class ServiceReportAirQuality {

    @Autowired
    private AirQualityRepository airQualityRepository;

    // pdf
    public byte[] generateAirQualityReport() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<AirQuality> aqs = airQualityRepository.findAll();

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("All AirQuality Data Report", fontTitle));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.addCell("Device ID");
            table.addCell("Air Pollution");
            table.addCell("timestamp");

            for (AirQuality aq : aqs) {
                table.addCell(String.valueOf(aq.getDeviceId()));
                table.addCell(String.valueOf(aq.getAirPollution()));
                table.addCell(String.valueOf(aq.getTimestamp()));
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF general fail: " + e.getMessage());
        }
    }

    public byte[] generateAirQualityReportBetweenTimes(int start, int end) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<AirQuality> aqs = airQualityRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("All AirQuality Data Report Between Times", fontTitle));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.addCell("Device ID");
            table.addCell("Air Pollution");
            table.addCell("timestamp");

            for (AirQuality aq : aqs) {
                table.addCell(String.valueOf(aq.getDeviceId()));
                table.addCell(String.valueOf(aq.getAirPollution()));
                table.addCell(String.valueOf(aq.getTimestamp()));
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF general fail: " + e.getMessage());
        }
    }

    // excel
    public byte[] generateAirQualityExcelReport() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            List<AirQuality> aqs = airQualityRepository.findAll();
            Sheet sheet = workbook.createSheet("AirQualityExcelReport");

            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            String[] columns = { "Device ID", "AirPollution", "timestamp" };

            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }
            int rowIdx = 1;
            for (AirQuality aq : aqs) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(aq.getDeviceId());
                row.createCell(1).setCellValue(aq.getAirPollution());
                row.createCell(2).setCellValue(aq.getTimestamp());
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

    public byte[] generateAirQualityExcelReportBetweenTimes(int start, int end) {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            List<AirQuality> aqs = airQualityRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
            Sheet sheet = workbook.createSheet("AirQualityExcelReportBetweenTimes");

            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            String[] columns = { "Device ID", "AirPollution", "timestamp" };

            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }
            int rowIdx = 1;
            for (AirQuality aq : aqs) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(aq.getDeviceId());
                row.createCell(1).setCellValue(aq.getAirPollution());
                row.createCell(2).setCellValue(aq.getTimestamp());
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
}