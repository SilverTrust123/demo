package com.example.demo.service.report;

import java.io.ByteArrayOutputStream;
import java.util.List;

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

@Service
public class ServiceReportCircuit {
    @Autowired
    private CircuitRepository circuitRepository;

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