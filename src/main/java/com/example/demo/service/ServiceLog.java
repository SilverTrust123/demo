package com.example.demo.service;

import java.io.ByteArrayOutputStream;
import com.example.demo.DTO.responseDTO.ResponseAllLogDTO;
import com.example.demo.DTO.responseDTO.ResponseLogDTO;
import com.example.demo.db.entity.Log;
import com.example.demo.db.repository.LogRepository;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.Font;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;

import io.github.cdimascio.dotenv.Dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceLog {
        @Autowired
        private LogRepository logRepo;
        Dotenv dotenv = Dotenv.load();
        private int DATA_LIMIT_NUMBER = Integer.parseInt(dotenv.get("DATA_LIMIT_NUMBER"));

        private static final Logger log = LoggerFactory.getLogger(ServiceLog.class);

        public void record(String level, String source, String message) {
                log.info("save log");
                Log log = new Log();
                log.setLogLevel(level);
                log.setSource(source);
                log.setMessage(message);
                log.setTimestamp((int) (System.currentTimeMillis() / 1000L));

                logRepo.save(log);
        }

        public byte[] generateAllReport() {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        List<Log> errors = logRepo.findAllByOrderByTimestampDesc(PageRequest.of(0, DATA_LIMIT_NUMBER));

                        Document document = new Document(PageSize.A4);
                        PdfWriter.getInstance(document, out);
                        document.open();

                        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                        document.add(new Paragraph("All Error And Warn Data Report", fontTitle));
                        document.add(new Paragraph(" "));
                        PdfPTable table = new PdfPTable(4);
                        table.setWidthPercentage(100);
                        table.addCell("loglevel");
                        table.addCell("source");
                        table.addCell("message");
                        table.addCell("timestamp");

                        for (Log err : errors) {
                                table.addCell(String.valueOf(err.getLogLevel()));
                                table.addCell(String.valueOf(err.getSource()));
                                table.addCell(String.valueOf(err.getMessage()));
                                table.addCell(String.valueOf(err.getTimestamp()));
                        }

                        document.add(table);
                        document.close();

                        return out.toByteArray();
                } catch (Exception e) {
                        throw new RuntimeException("PDF general fail: " + e.getMessage());
                }
        }

        public List<ResponseLogDTO> getAllLogs() {
                return logRepo.findAllByOrderByTimestampDesc(PageRequest.of(0, DATA_LIMIT_NUMBER))
                                .stream()
                                .map(entity -> new ResponseLogDTO(
                                                entity.getId(),
                                                entity.getLogLevel(),
                                                entity.getSource(),
                                                entity.getMessage(),
                                                entity.getTimestamp()))
                                .collect(Collectors.toList());
        }

        public byte[] generateErrorReport() {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        List<Log> errors = logRepo.findByLogLevelOrderByTimestampDesc("ERROR");

                        Document document = new Document(PageSize.A4);
                        PdfWriter.getInstance(document, out);
                        document.open();

                        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                        document.add(new Paragraph("All Error Data Report", fontTitle));
                        document.add(new Paragraph(" "));
                        PdfPTable table = new PdfPTable(4);
                        table.setWidthPercentage(100);
                        table.addCell("loglevel");
                        table.addCell("source");
                        table.addCell("message");
                        table.addCell("timestamp");

                        for (Log err : errors) {
                                table.addCell(String.valueOf(err.getLogLevel()));
                                table.addCell(String.valueOf(err.getSource()));
                                table.addCell(String.valueOf(err.getMessage()));
                                table.addCell(String.valueOf(err.getTimestamp()));
                        }

                        document.add(table);
                        document.close();

                        return out.toByteArray();
                } catch (Exception e) {
                        throw new RuntimeException("PDF general fail: " + e.getMessage());
                }
        }

        public ResponseAllLogDTO getErrorLogs() {
                return new ResponseAllLogDTO(logRepo.findByLogLevelOrderByTimestampDesc("ERROR").stream()
                                .map(entity -> new ResponseLogDTO(entity.getId(),
                                                entity.getLogLevel(),
                                                entity.getSource(),
                                                entity.getMessage(),
                                                entity.getTimestamp()))
                                .collect(Collectors.toList()));
        }

        public byte[] generateWarnReport() {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        List<Log> errors = logRepo.findByLogLevelOrderByTimestampDesc("WARN");

                        Document document = new Document(PageSize.A4);
                        PdfWriter.getInstance(document, out);
                        document.open();

                        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                        document.add(new Paragraph("All Warn Data Report", fontTitle));
                        document.add(new Paragraph(" "));
                        PdfPTable table = new PdfPTable(4);
                        table.setWidthPercentage(100);
                        table.addCell("loglevel");
                        table.addCell("source");
                        table.addCell("message");
                        table.addCell("timestamp");

                        for (Log err : errors) {
                                table.addCell(String.valueOf(err.getLogLevel()));
                                table.addCell(String.valueOf(err.getSource()));
                                table.addCell(String.valueOf(err.getMessage()));
                                table.addCell(String.valueOf(err.getTimestamp()));
                        }

                        document.add(table);
                        document.close();

                        return out.toByteArray();
                } catch (Exception e) {
                        throw new RuntimeException("PDF general fail: " + e.getMessage());
                }
        }

        public ResponseAllLogDTO getWarnLogs() {
                return new ResponseAllLogDTO(logRepo.findByLogLevelOrderByTimestampDesc("WARN").stream()
                                .map(entity -> new ResponseLogDTO(entity.getId(),
                                                entity.getLogLevel(),
                                                entity.getSource(),
                                                entity.getMessage(),
                                                entity.getTimestamp()))
                                .collect(Collectors.toList()));
        }

        public ResponseAllLogDTO getLogsByTime(int start, int end) {
                return new ResponseAllLogDTO(logRepo.findByTimestampBetweenOrderByTimestampDesc(start, end).stream()
                                .map(entity -> new ResponseLogDTO(entity.getId(),
                                                entity.getLogLevel(),
                                                entity.getSource(),
                                                entity.getMessage(),
                                                entity.getTimestamp()))
                                .collect(Collectors.toList()));
        }

        public String truncateAllLog() {
                logRepo.deleteAllLogs();
                log.info("secces truncate the log");
                return new String("secceses");
        }
}