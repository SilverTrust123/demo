package com.example.demo.service.report;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.demo.db.entity.Log;
import com.example.demo.db.repository.LogRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class ServiceReportLog {

    @Autowired
    private LogRepository logRepository;
    Dotenv dotenv = Dotenv.load();
    private int DATA_LIMIT_NUMBER = Integer.parseInt(dotenv.get("DATA_LIMIT_NUMBER"));

    public byte[] generateLogReport() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<Log> logs = logRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, DATA_LIMIT_NUMBER));

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("System Log Report", fontTitle));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("ID");
            table.addCell("Level");
            table.addCell("Source");
            table.addCell("Message");

            for (Log log : logs) {
                table.addCell(String.valueOf(log.getId()));
                table.addCell(log.getLogLevel());
                table.addCell(log.getSource());
                table.addCell(log.getMessage());
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF 產生失敗: " + e.getMessage());
        }
    }

    public byte[] generateLogReportBetweenTimes(int start, int end) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<Log> logs = logRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("System Log Report Between Times", fontTitle));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("ID");
            table.addCell("Level");
            table.addCell("Source");
            table.addCell("Message");

            for (Log log : logs) {
                table.addCell(String.valueOf(log.getId()));
                table.addCell(log.getLogLevel());
                table.addCell(log.getSource());
                table.addCell(log.getMessage());
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF 產生失敗: " + e.getMessage());
        }
    }
}