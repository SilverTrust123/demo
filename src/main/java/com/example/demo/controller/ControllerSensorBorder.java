package com.example.demo.controller;

import java.util.concurrent.CompletableFuture;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.DTO.requestDTO.*;
import com.example.demo.priorityQueueTask.QueueService;
import com.example.demo.service.ServiceDelayTime;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sensorBorder")
public class ControllerSensorBorder {

    private static final Logger log = LoggerFactory.getLogger(ControllerSensorBorder.class);
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

    @GetMapping("/getCurrentSensorBorder")
    public CompletableFuture<Object> getCurrentSensorBorder() throws Exception {
        long curr = System.currentTimeMillis();
        log.info("receive and transfer the getCurrentSensorBorder request ");
        return queueService.addRequestToQueue(IMPORTANT, null, "getCurrentSensorBorder").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("getCurrentSensorBorder done in " + (done - curr));
            DT.logInLastestProcessTime((int) (done - curr));
        });
    }

    @GetMapping("/saveSensorBorder")
    public CompletableFuture<Object> saveSensorBorder(RequestSensorBorderDTO req) throws Exception {
        long curr = System.currentTimeMillis();
        log.info("receive and transfer the saveSensorBorder request ");
        return queueService.addRequestToQueue(IMPORTANT, req, "saveSensorBorder").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("saveSensorBorder done in " + (done - curr));
            DT.logInLastestProcessTime((int) (done - curr));
        });
    }
}
