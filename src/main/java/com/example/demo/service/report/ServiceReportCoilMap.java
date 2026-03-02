package com.example.demo.service.report;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.example.demo.plc.*;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class ServiceReportCoilMap {
    private final PointList pointList = new PointList();

    public byte[] generatePLCCoilAddressAllocationTable() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("PLC Coil Address Allocation Table", fontTitle));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("Coil Type");
            table.addCell("Name");
            table.addCell("Detail");
            table.addCell("Address");

            Map<String, MPoint> ms = pointList.getMPointMap();
            Map<String, DPoint> ds = pointList.getDPointMap();

            for (Map.Entry<String, MPoint> entry : ms.entrySet()) {
                String key = entry.getKey();
                MPoint value = entry.getValue();
                table.addCell("MPoint");
                table.addCell(key);
                table.addCell(value.getName());
                table.addCell(String.valueOf(value.getAddress()));
            }
            for (Map.Entry<String, DPoint> entry : ds.entrySet()) {
                String key = entry.getKey();
                DPoint value = entry.getValue();
                table.addCell("DPoint");
                table.addCell(key);
                table.addCell(value.getName());
                table.addCell(String.valueOf(value.getAddress()));
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF general fail: " + e.getMessage());
        }
    }
}