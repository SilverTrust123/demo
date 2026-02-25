package com.example.demo.controller;

import java.util.concurrent.CompletableFuture;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.priorityQueueTask.QueueService;
import org.slf4j.Logger;

@RestController
@RequestMapping("/log")
public class ControllerLog {
    private static final Logger log = LoggerFactory.getLogger(ControllerLog.class);
    @Value("${important}")
    private int IMPORTANT;
    @Value("${normal}")
    private int NORMAL;
    @Value("${urgent}")
    private int URGENT;
    @Autowired
    private QueueService queueService;

    @GetMapping("/all")
    public CompletableFuture<Object> getAllLogs() throws Exception {
        log.info("Received and transfer request for All logs ");
        return queueService.addRequestToQueue(URGENT, null, "getAllLogs");
    }
}
