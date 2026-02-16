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

import com.example.demo.priorityQueueTask.QueueService;
import com.example.demo.sensor.SensorDataTemperatureAndHumidity;

import org.slf4j.Logger;

@RestController
@RequestMapping("/temperatureAndHumidity")
public class ControllerTemparatureAndHumidity {
    private static final Logger log = LoggerFactory.getLogger(ControllerTemparatureAndHumidity.class);
    @Value("${IMPORTANT:50}")
    private int IMPORTANT;
    @Value("${NORMAL:100}")
    private int NORMAL;
    @Value("${URGENT:10}")
    private int URGENT;

    @Autowired
    private QueueService queueService;

    @PostMapping("/TemparatureAndHumidityData")
    public CompletableFuture<Object> receiveTemparatureAndHumidityData(
            @RequestBody SensorDataTemperatureAndHumidity data) {
        log.info("Received and put in priority queue and transfer to service");
        return queueService.addRequestToQueue(NORMAL, data, "receiveTemparatureAndHumidityData");
    }

    // 船空的回去就是找不到東西
    @GetMapping("/TemparatureAndHumidityData/{deviceId}")
    public CompletableFuture<Object> getTemparatureAndHumidityData(@PathVariable String deviceId) {
        log.info("Received and transfer request for temperature and humidity data of device");
        return queueService.addRequestToQueue(NORMAL, deviceId, "getTemparatureAndHumidityData");
    }

    // 打掉太舊的資料 然後回傳剩下的 可能會只剩一個 等一下要跟董事長說一下
    @GetMapping("/TemparatureAndHumidityData")
    public CompletableFuture<Object> getAllTemparatureAndHumidityData() {
        log.info("Received and transfer request for all temperature and humidity data");
        return queueService.addRequestToQueue(IMPORTANT, null, "getAllTemparatureAndHumidityData");
    }
}
