package com.example.demo.controller;

import java.util.concurrent.CompletableFuture;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.priorityQueueTask.QueueService;
import com.example.demo.DTO.requestDTO.history.*;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/history")
public class ControllerHistoryData {
    private static final Logger log = LoggerFactory.getLogger(ControllerHistoryData.class);
    @Value("${important}")
    private int IMPORTANT;
    @Value("${normal}")
    private int NORMAL;
    @Value("${urgent}")
    private int URGENT;
    @Autowired
    private QueueService queueService;

    @PostMapping("/temparatureAndHumidityHistory")
    public CompletableFuture<Object> getTemparatureAndHumidityHistory(
            @RequestBody RequestHistoryTemparatureAndHumidityDTO request) throws Exception {
        log.info("Received and transfer request for getTemparatureAndHumidity ");
        return queueService.addRequestToQueue(IMPORTANT, request, "getTemparatureAndHumidityHistory");
    }

    @PostMapping("/circuitHistory")
    public CompletableFuture<Object> getCircuitHistory(@RequestBody RequestHistoryCircuitDTO request) throws Exception {
        log.info("Received and transfer circuit data to logic");
        return queueService.addRequestToQueue(IMPORTANT, request, "getCircuitHistory");
    }

    @PostMapping("/airQualityHistory")
    public CompletableFuture<Object> getAirQualityHistory(@RequestBody RequestHistoryAirQualityDTO request)
            throws Exception {
        log.info("Received and transfer air quality data to logic");
        return queueService.addRequestToQueue(IMPORTANT, request, "getAirQualityHistory");
    }

    @PostMapping("/airParticulatesHistory")
    public CompletableFuture<Object> getAirParticulatesHistory(@RequestBody RequestHistoryAirParticulatesDTO request)
            throws Exception {
        log.info("Received and transfer air particulates data to logic");
        return queueService.addRequestToQueue(IMPORTANT, request, "getAirParticulatesHistory");
    }
}
