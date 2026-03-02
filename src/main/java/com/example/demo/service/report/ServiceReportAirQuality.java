package com.example.demo.service.report;

import java.io.ByteArrayOutputStream;
import java.util.List;

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
}