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
@RequestMapping("/allData")
public class ControllerData {
    private static final Logger log = LoggerFactory.getLogger(ControllerData.class);
    @Value("${important}")
    private int IMPORTANT;
    @Value("${normal}")
    private int NORMAL;
    @Value("${urgent}")
    private int URGENT;
    @Autowired
    private QueueService queueService;

    @GetMapping("/")
    public CompletableFuture<Object> AllData() throws Exception {
        log.info("Received and transfer request for all data ");
        return queueService.addRequestToQueue(IMPORTANT, null, "ALLData");
    }
}
