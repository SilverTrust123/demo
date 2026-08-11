package com.example.demo.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.priorityQueueTask.QueueService;
import com.example.demo.DTO.requestDTO.RequestCircuitDTO;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;

@RestController
@RequestMapping("/circuitData")
public class ControllerCircuit {
    private static final Logger log = LoggerFactory.getLogger(ControllerCircuit.class);
    @Value("${important}")
    private int IMPORTANT;
    @Value("${normal}")
    private int NORMAL;
    @Value("${urgent}")
    private int URGENT;
    @Autowired
    private QueueService queueService;

    @PostMapping("/")
    public CompletableFuture<Object> receiveCircuitData(@RequestBody RequestCircuitDTO data) {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer circuit data");
        return queueService.addRequestToQueue(NORMAL, data, "receiveCircuitData").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("receive circuit data done in " + (done - curr));
        });
    }

    // 船空的回去就是找不到東西 或是太舊了
    @GetMapping("/{deviceId}")
    public CompletableFuture<Object> getCircuitData(@PathVariable String deviceId) {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for circuit data of device");
        return queueService.addRequestToQueue(IMPORTANT, deviceId, "getCircuitData").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("get circuit data by id done in " + (done - curr));
        });
    }

    // 這個本來最多就一個 如果船空的回去就是太舊了
    @GetMapping("/")
    public CompletableFuture<Object> getAllCircuitData() {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for all circuit data");
        return queueService.addRequestToQueue(IMPORTANT, null, "getAllCircuitData").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("get circuit data done in " + (done - curr));
        });
    }

    @GetMapping("/truncateAllCircuitData")
    public CompletableFuture<Object> truncateAllCircuitData() {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for truncate all circuit data");
        return queueService.addRequestToQueue(IMPORTANT, null, "truncateAllCircuitData").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("get truncate circuit data done in " + (done - curr));
        });
    }
}
