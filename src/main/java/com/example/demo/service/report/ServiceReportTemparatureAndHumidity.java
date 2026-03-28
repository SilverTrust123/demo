package com.example.demo.service.report;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.db.entity.TemperatureAndHumidity;
import com.example.demo.db.repository.TemperatureAndHumidityRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;

@Service
public class ServiceReportTemparatureAndHumidity {

    @Autowired
    private TemperatureAndHumidityRepository temperatureAndHumidityRepository;

    // excel
    public byte[] generateTemperatureAndHumidityExcelReport() {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            List<TemperatureAndHumidity> temps = temperatureAndHumidityRepository.findAll();
            Sheet sheet = workbook.createSheet("TempAndHumidityReport");

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
            for (TemperatureAndHumidity temp : temps) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(temp.getDeviceId());
                row.createCell(1).setCellValue(temp.getTemperature());
                row.createCell(2).setCellValue(temp.getHumidity());
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

    public byte[] generateTemperatureAndHumidityExcelReportBetweenTimes(int start, int end) {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            List<TemperatureAndHumidity> temps = temperatureAndHumidityRepository
                    .findByTimestampBetweenOrderByTimestampDesc(start, end);
            Sheet sheet = workbook.createSheet("TempAndHumidityReport");

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
            for (TemperatureAndHumidity temp : temps) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(temp.getDeviceId());
                row.createCell(1).setCellValue(temp.getTemperature());
                row.createCell(2).setCellValue(temp.getHumidity());
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
            List<TemperatureAndHumidity> temps = temperatureAndHumidityRepository.findAll();

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("All Temperature And Humidity History Data Report", fontTitle));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.addCell("Device ID");
            table.addCell("Temperature");
            table.addCell("Humidity");

            for (TemperatureAndHumidity temp : temps) {
                table.addCell(String.valueOf(temp.getDeviceId()));
                table.addCell(String.valueOf(temp.getTemperature()));
                table.addCell(String.valueOf(temp.getHumidity()));
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF general fail: " + e.getMessage());
        }
    }

    public byte[] generateTemparatureAndHumidityReportBetweenTimes(int start, int end) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<TemperatureAndHumidity> temps = temperatureAndHumidityRepository
                    .findByTimestampBetweenOrderByTimestampDesc(start, end);

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("All Temperature And Humidity History Data Report Between Times", fontTitle));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.addCell("Device ID");
            table.addCell("Temperature");
            table.addCell("Humidity");

            for (TemperatureAndHumidity temp : temps) {
                table.addCell(String.valueOf(temp.getDeviceId()));
                table.addCell(String.valueOf(temp.getTemperature()));
                table.addCell(String.valueOf(temp.getHumidity()));
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF general fail: " + e.getMessage());
        }
    }
}