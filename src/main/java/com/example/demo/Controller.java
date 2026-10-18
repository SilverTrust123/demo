package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import com.example.demo.service.ServiceDelayTime;
import org.springframework.http.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @CrossOrigin(origins = "*")
@RestController
public class Controller {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    @Value("${urgent}")
    private int URGENT;
    @Autowired
    private ServiceDelayTime DT;

    @GetMapping("/check")
    public String checkConnect() {
        log.info("receive frontend check the backend");
        return "backend running";
    }

    @GetMapping("/latestLog")
    public ResponseEntity<List<String>> getLatestLogs(
            @RequestParam(defaultValue = "100") int lines) throws IOException {

        Path path = Paths.get("magicSystem.log");

        List<String> allLines = Files.readAllLines(path);

        int fromIndex = Math.max(0, allLines.size() - lines);

        List<String> result = allLines.subList(fromIndex, allLines.size());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/fullLog")
    public ResponseEntity<String> getfullLogs() throws IOException {

        Path path = Paths.get("magicSystem.log");

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        String content = Files.readString(path);

        return ResponseEntity.ok(content);
    }

    private byte[] innerGetfullLogs() throws IOException {
        Path path = Paths.get("magicSystem.log");
        byte[] fileBytes = Files.readAllBytes(path);
        return fileBytes;
    }

    @GetMapping("/detailLog")
    public ResponseEntity<byte[]> getDetailLogReport() throws Exception {
        long start = System.currentTimeMillis();
        log.info("Received and transfer request for logs report");

        byte[] pdfContent = innerGetfullLogs();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "DetailLogReport.log");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        long done = System.currentTimeMillis();
        long duration = done - start;
        log.info("get log report done in " + duration + " ms");
        DT.logInLastestProcessTime((int) duration);
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

}
