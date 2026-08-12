package com.example.demo.controller;

import java.util.concurrent.CompletableFuture;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.DTO.requestDTO.RequestLogDTO;
import com.example.demo.priorityQueueTask.QueueService;
import com.example.demo.service.ServiceDelayTime;

import org.slf4j.Logger;

@RestController
@RequestMapping("/log")
public class ControllerLog {
    private static final Logger log = LoggerFactory.getLogger(ControllerLog.class);
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

    @GetMapping("/all")
    public CompletableFuture<Object> getAllLogs() throws Exception {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for All logs ");
        return queueService.addRequestToQueue(URGENT, null, "getAllLogs").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("get all logs done in " + (done - curr));
            DT.logInLastestProcessTime((int) (done - curr));
        });
    }

    @GetMapping("/error")
    public CompletableFuture<Object> getErrorLogs() throws Exception {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for All error logs ");
        return queueService.addRequestToQueue(URGENT, null, "getErrorLogs").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("get error logs done in " + (done - curr));
            DT.logInLastestProcessTime((int) (done - curr));
        });
    }

    @GetMapping("/warn")
    public CompletableFuture<Object> getWarnLogs() throws Exception {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for All warn logs ");
        return queueService.addRequestToQueue(URGENT, null, "getWarnLogs").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("get warn logs done in " + (done - curr));
            DT.logInLastestProcessTime((int) (done - curr));
        });
    }

    @GetMapping("/byTime")
    public CompletableFuture<Object> getLogByTime(@RequestBody RequestLogDTO request) throws Exception {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for getLogByTime");
        return queueService.addRequestToQueue(URGENT, request, "getLogByTime").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("get log by time done in " + (done - curr));
            DT.logInLastestProcessTime((int) (done - curr));
        });
    }

    @GetMapping("/truncateAllLog")
    public CompletableFuture<Object> truncateAllLog() throws Exception {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for truncateAllLog");
        return queueService.addRequestToQueue(URGENT, null, "truncateAllLog").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("truncate All Log done in " + (done - curr));
            DT.logInLastestProcessTime((int) (done - curr));
        });
    }

}
