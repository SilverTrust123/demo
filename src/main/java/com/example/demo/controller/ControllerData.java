package com.example.demo.controller;

import java.util.concurrent.CompletableFuture;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.priorityQueueTask.QueueService;
import com.example.demo.service.ServiceDelayTime;

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
    @Autowired
    private ServiceDelayTime DT;

    @GetMapping("/")
    public CompletableFuture<Object> AllData() throws Exception {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for all data ");
        return queueService.addRequestToQueue(IMPORTANT, null, "ALLData").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("get all data done in " + (done - curr));
            DT.logInLastestProcessTime((int) (done - curr));
        });
    }

    @GetMapping("/allDataAndDeviceState")
    public CompletableFuture<Object> AllDataAndDeviceState() {
        long req = System.currentTimeMillis();
        log.info("Received and transfer request for all data and device state");
        return queueService.addRequestToQueue(IMPORTANT, null, "AllDataAndDeviceState").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("all data and device status request done in " + (done - req));
            DT.logInLastestProcessTime((int) (done - req));
        });
    }

    @GetMapping("/allDataAndDeviceStateWithoutPLC")
    public CompletableFuture<Object> AllDataAndDeviceStateWithoutPLC() {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for all data and device state without plc ");
        return queueService.addRequestToQueue(IMPORTANT, null, "AllDataAndDeviceStateWithoutPLC")
                .whenComplete((res, exp) -> {
                    long done = System.currentTimeMillis();
                    log.info("all data and device status without PLC done in " + (done - curr));
                    DT.logInLastestProcessTime((int) (done - curr));
                });
    }

    @GetMapping("/allSenosrData")
    public CompletableFuture<Object> AllSensorData() {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for all sensor data ");
        return queueService.addRequestToQueue(IMPORTANT, null, "AllSensorData").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("all sensor data done in " + (done - curr));
            DT.logInLastestProcessTime((int) (done - curr));
        });
    }
}
