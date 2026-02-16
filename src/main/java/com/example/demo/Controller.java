package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "*")
@RestController
public class Controller {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    @GetMapping("/")
    public String checkConnect() {
        log.info("receive frontend check the backend");
        return "backend running";
    }

    @GetMapping("/latestLog")
    public ResponseEntity<List<String>> getLatestLogs(
            @RequestParam(defaultValue = "100") int lines) throws IOException {

        Path path = Paths.get("logs/app.log");

        List<String> allLines = Files.readAllLines(path);

        int fromIndex = Math.max(0, allLines.size() - lines);

        List<String> result = allLines.subList(fromIndex, allLines.size());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/fullLog")
    public ResponseEntity<String> getLogs() throws IOException {

        Path path = Paths.get("logs/app.log");

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        String content = Files.readString(path);

        return ResponseEntity.ok(content);
    }

}
