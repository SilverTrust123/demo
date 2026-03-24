package com.example.demo.controller;

import java.util.concurrent.CompletableFuture;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.priorityQueueTask.QueueService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Load")
public class ControllerLoad {

    private static final Logger log = LoggerFactory.getLogger(ControllerLoad.class);
    @Value("${important}")
    private int IMPORTANT;
    @Value("${normal}")
    private int NORMAL;
    @Value("${urgent}")
    private int URGENT;
    @Autowired
    private QueueService queueService;

    @GetMapping("/loadStats")
    public CompletableFuture<Object> getLoadStats() throws Exception {
        log.info("receive and transfer the Load Stats request ");
        return queueService.addRequestToQueue(IMPORTANT, null, "loadStats");
    }
}
