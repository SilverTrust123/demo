package com.example.demo.service.report;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}