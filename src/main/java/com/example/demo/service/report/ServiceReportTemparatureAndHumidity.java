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

@Service
public class ServiceReportTemparatureAndHumidity {

    @Autowired
    private TemperatureAndHumidityRepository temperatureAndHumidityRepository;

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
}