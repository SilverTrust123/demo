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
@RequestMapping("/deviceState")
public class ControllerDeviceState {
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
    public CompletableFuture<Object> AllDeviceState() throws Exception {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for AllDeviceState ");
        return queueService.addRequestToQueue(IMPORTANT, null, "AllDeviceState").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("all device state done in " + (done - curr));
            DT.logInLastestProcessTime((int) (done - curr));
        });
    }
}
