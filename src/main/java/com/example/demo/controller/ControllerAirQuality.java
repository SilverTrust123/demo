package com.example.demo.controller;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DTO.requestDTO.RequestAirQualityDTO;
import com.example.demo.priorityQueueTask.QueueService;

@RestController
@RequestMapping("/airQualityData")
public class ControllerAirQuality {
    private static final Logger log = LoggerFactory.getLogger(ControllerAirQuality.class);
    @Value("${important}")
    private int IMPORTANT;
    @Value("${normal}")
    private int NORMAL;
    @Value("${urgent}")
    private int URGENT;
    @Autowired
    private QueueService queueService;

    @PostMapping("/")
    public CompletableFuture<Object> recriveAirQuality(@RequestBody RequestAirQualityDTO data) {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer air quality data");
        return queueService.addRequestToQueue(NORMAL, data, "recriveAirQuality").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("receive air quality data done in " + (done - curr));
        });
    }

    @GetMapping("/{deviceId}")
    public CompletableFuture<Object> getAirQualityData(@PathVariable String deviceId) {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for air quality data of device");
        return queueService.addRequestToQueue(IMPORTANT, deviceId, "getAirQualityData").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("get air quality data rerquest by id done in " + (done - curr));
        });
    }

    @GetMapping("/")
    public CompletableFuture<Object> getAllAirQualityData() {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for all air quality data");
        return queueService.addRequestToQueue(IMPORTANT, null, "getAllAirQualityData").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("get all air quality done in " + (done - curr));
        });
    }
}
