package com.example.demo.controller;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.priorityQueueTask.QueueService;
import com.example.demo.sensor.SensorDataTemperatureAndHumidity;
import com.example.demo.service.ServiceTemparatureAndHumidity;
import org.slf4j.Logger;

@RestController
@RequestMapping("/temperatureAndHumidity")
public class ControllerTemparatureAndHumidity {
    private static final Logger log = LoggerFactory.getLogger(ControllerTemparatureAndHumidity.class);

    @Autowired
    private ServiceTemparatureAndHumidity serviceTemparatureAndHumidity;
    @Autowired
    private QueueService queueService;

    @PostMapping("/TemparatureAndHumidityData")
    public CompletableFuture<Object> receiveTemparatureAndHumidityData(
            @RequestBody SensorDataTemperatureAndHumidity data) {
        int priority = 10;
        log.info("Received and put in priority queue and transfer to service");
        return queueService.addRequestToQueue(priority, data);
    }

    // 船空的回去就是找不到東西
    @GetMapping("/TemparatureAndHumidityData/{deviceId}")
    public SensorDataTemperatureAndHumidity getTemparatureAndHumidityData(@PathVariable String deviceId) {
        log.info("Received and transfer request for temperature and humidity data of device");
        return serviceTemparatureAndHumidity.getTemparatureAndHumidityData(deviceId);
    }

    // 打掉太舊的資料 然後回傳剩下的 可能會只剩一個 等一下要跟董事長說一下
    @GetMapping("/TemparatureAndHumidityData")
    public Collection<SensorDataTemperatureAndHumidity> getAllTemparatureAndHumidityData() {
        log.info("Received and transfer request for all temperature and humidity data");
        return serviceTemparatureAndHumidity.getAllTemparatureAndHumidityData();
    }
}
