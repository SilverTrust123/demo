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

import com.example.demo.DTO.requestDTO.RequestTemperatureAndHumidityDTO;

import com.example.demo.priorityQueueTask.QueueService;

import org.slf4j.Logger;

@RestController
@RequestMapping("/temperatureAndHumidityData")
public class ControllerTemparatureAndHumidity {
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
    public CompletableFuture<Object> receiveTemparatureAndHumidityData(
            @RequestBody RequestTemperatureAndHumidityDTO data) {
        long curr = System.currentTimeMillis();
        log.info("Received and put in priority queue and transfer to service");
        return queueService.addRequestToQueue(NORMAL, data, "receiveTemparatureAndHumidityData")
                .whenComplete((res, exp) -> {
                    long done = System.currentTimeMillis();
                    log.info("receive temperature and humidity data done in " + (done - curr));
                });
    }

    // 船空的回去就是找不到東西
    @GetMapping("/{deviceId}")
    public CompletableFuture<Object> getTemparatureAndHumidityData(@PathVariable String deviceId) {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for temperature and humidity data of device");
        return queueService.addRequestToQueue(NORMAL, deviceId, "getTemparatureAndHumidityData")
                .whenComplete((res, exp) -> {
                    long done = System.currentTimeMillis();
                    log.info("get temperature and humidity data by id done in " + (done - curr));
                });
    }

    // 打掉太舊的資料 然後回傳剩下的 可能會只剩一個 等一下要跟董事長說一下
    @GetMapping("/")
    public CompletableFuture<Object> getAllTemparatureAndHumidityData() {
        long curr = System.currentTimeMillis();
        log.info("Received and transfer request for all temperature and humidity data");
        return queueService.addRequestToQueue(IMPORTANT, null, "getAllTemparatureAndHumidityData")
                .whenComplete((res, exp) -> {
                    long done = System.currentTimeMillis();
                    log.info("get all temperature and humidity data done in " + (done - curr));
                });
    }
}
