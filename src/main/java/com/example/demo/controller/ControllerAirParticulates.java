package com.example.demo.controller;

import java.util.concurrent.CompletableFuture;

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
import com.example.demo.priorityQueueTask.QueueService;

import org.slf4j.Logger;

@RestController
@RequestMapping("/airParticulatesData")
public class ControllerAirParticulates {
    private static final Logger log = LoggerFactory.getLogger(ControllerAirParticulates.class);
    @Value("${important}")
    private int IMPORTANT;
    @Value("${normal}")
    private int NORMAL;
    @Value("${urgent}")
    private int URGENT;
    @Autowired
    private QueueService queueService;

    @PostMapping("/")
    public CompletableFuture<Object> recriveAirPartical(@RequestBody RequestAirParticulatesDTO data) {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer air particulates data");
        return queueService.addRequestToQueue(NORMAL, data, "recriveAirPartical").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("rerceive airparticles data done in " + (done - curr));
        });
    }

    @GetMapping("/{deviceId}")
    public CompletableFuture<Object> getAirParticalData(@PathVariable String deviceId) {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for air particulates data of device");
        return queueService.addRequestToQueue(IMPORTANT, deviceId, "getAirParticalData").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("get aitparticle by device id done in " + (done - curr));
        });
    }

    @GetMapping("/")
    public CompletableFuture<Object> getAllAirParticalData() {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for all air particulates data");
        return queueService.addRequestToQueue(IMPORTANT, null, "getAllAirParticalData").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("get ALL airparticle data request done in " + (done - curr));
        });
    }

    @GetMapping("/truncateAllAirParticulatesData")
    public CompletableFuture<Object> truncateAllAirParticulatesData() {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for truncate all air particulates data");
        return queueService.addRequestToQueue(IMPORTANT, null, "truncateAllAirParticulatesData")
                .whenComplete((res, exp) -> {
                    long done = System.currentTimeMillis();
                    log.info("get truncate ALL airparticle data request done in " + (done - curr));
                });
    }
}