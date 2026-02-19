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

import com.example.demo.DTO.requestDTO.RequestAirParticulatesDTO;
import com.example.demo.DTO.responseDTO.ResponseAirParticulatesDTO;
import com.example.demo.priorityQueueTask.QueueService;

@RestController
@RequestMapping("/airQualityData")
public class ControllerAirQuality {
    private static final Logger log = LoggerFactory.getLogger(ControllerTemparatureAndHumidity.class);
    @Value("${important}")
    private int IMPORTANT;
    @Value("${normal}")
    private int NORMAL;
    @Value("${urgent}")
    private int URGENT;
    @Autowired
    private QueueService queueService;

    @PostMapping("/")
    public CompletableFuture<Object> recriveAirQuality(@RequestBody RequestAirParticulatesDTO data) {
        log.info("Received and transfer air quality data");
        return queueService.addRequestToQueue(NORMAL, data, "recriveAirQuality");
    }

    @GetMapping("/{deviceId}")
    public CompletableFuture<Object> getAirQualityData(@PathVariable String deviceId) {
        log.info("Received and transfer request for air quality data of device");
        return queueService.addRequestToQueue(IMPORTANT, deviceId, "getAirQualityData");
    }

    @GetMapping("/")
    public CompletableFuture<Object> getAllAirQualityData() {
        log.info("Received and transfer request for all air quality data");
        return queueService.addRequestToQueue(IMPORTANT, null, "getAllAirQualityData");
    }
}
