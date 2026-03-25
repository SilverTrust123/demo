package com.example.demo.service.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;

import com.example.demo.db.entity.AirParticulates;
import com.example.demo.db.repository.*;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class ServiceReportAirParticulates {

    @Autowired
    private AirParticulatesRepository airParticulatesRepository;

    // pdf
    public byte[] generateAirQualityReport() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<AirParticulates> aps = airParticulatesRepository.findAll();

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("All AirParticulates Data Report", fontTitle));
            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.addCell("Device ID");
            table.addCell("pm2_5");
            table.addCell("timestamp");

            for (AirParticulates ap : aps) {
                table.addCell(String.valueOf(ap.getDeviceId()));
                table.addCell(String.valueOf(ap.getPm2_5()));
                table.addCell(String.valueOf(ap.getTimestamp()));
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
            List<AirParticulates> aps = airParticulatesRepository.findByTimestampBetweenOrderByTimestampDesc(start,
                    end);

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("All AirParticulates Data Report Between Times", fontTitle));
            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.addCell("Device ID");
            table.addCell("pm2_5");
            table.addCell("timestamp");

            for (AirParticulates ap : aps) {
                table.addCell(String.valueOf(ap.getDeviceId()));
                table.addCell(String.valueOf(ap.getPm2_5()));
                table.addCell(String.valueOf(ap.getTimestamp()));
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF general fail: " + e.getMessage());
        }
    }

    // excel
    public byte[] generateAirParticulatesExcelReport() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            List<AirParticulates> aps = airParticulatesRepository.findAll();
            Sheet sheet = workbook.createSheet("AirParticulatesExcelReport");

            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            String[] columns = { "Device ID", "pm2_5", "timestamp" };

            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }
            int rowIdx = 1;
            for (AirParticulates ap : aps) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(ap.getDeviceId());
                row.createCell(1).setCellValue(ap.getPm2_5());
                row.createCell(2).setCellValue(ap.getTimestamp());
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

    public byte[] generateAirParticulatesExcelReportBetweenTimes(int start, int end) {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            List<AirParticulates> aps = airParticulatesRepository.findByTimestampBetweenOrderByTimestampDesc(start,
                    end);
            Sheet sheet = workbook.createSheet("AirParticulatesExcelReportBetweenTimes");

            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            String[] columns = { "Device ID", "pm2_5", "timestamp" };

            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }
            int rowIdx = 1;
            for (AirParticulates ap : aps) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(ap.getDeviceId());
                row.createCell(1).setCellValue(ap.getPm2_5());
                row.createCell(2).setCellValue(ap.getTimestamp());
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