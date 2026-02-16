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
import com.example.demo.sensor.SensorDataCircuit;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;

@RestController
@RequestMapping("/circuit")
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

    @PostMapping("/CircuitData")
    public CompletableFuture<Object> receiveCircuitData(@RequestBody SensorDataCircuit data) {
        log.info("Received and transfer circuit data");
        return queueService.addRequestToQueue(NORMAL, data, "receiveCircuitData");
    }

    // 船空的回去就是找不到東西 或是太舊了
    @GetMapping("/CircuitData/{deviceId}")
    public CompletableFuture<Object> getCircuitData(@PathVariable String deviceId) {
        log.info("Received and transfer request for circuit data of device");
        return queueService.addRequestToQueue(IMPORTANT, deviceId, "getCircuitData");
    }

    // 這個本來最多就一個 如果船空的回去就是太舊了
    @GetMapping("/CircuitData")
    public CompletableFuture<Object> getAllCircuitData() {
        log.info("Received and transfer request for all circuit data");
        return queueService.addRequestToQueue(IMPORTANT, null, "getAllCircuitData");
    }
}
