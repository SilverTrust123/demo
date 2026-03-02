package com.example.demo.controller;

import java.util.concurrent.CompletableFuture;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.priorityQueueTask.QueueService;

import org.slf4j.Logger;

@RestController
@RequestMapping("/report")
public class ControllerReport {
    private static final Logger log = LoggerFactory.getLogger(ControllerLog.class);
    @Value("${important}")
    private int IMPORTANT;
    @Value("${normal}")
    private int NORMAL;
    @Value("${urgent}")
    private int URGENT;
    @Autowired
    private QueueService queueService;

    @GetMapping("/log")

    public CompletableFuture<ResponseEntity<byte[]>> getLogReport() throws Exception {
        log.info("Received and transfer request for logs report ");
        return queueService
                .addRequestToQueue(URGENT, null, "getLogReport")
                .thenApply(pdfContent -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment", "log_report.pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(
                            (byte[]) pdfContent,
                            headers,
                            HttpStatus.OK);
                });

    }
}
