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

import com.example.demo.DTO.requestDTO.RequestCamDTO;
import com.example.demo.priorityQueueTask.QueueService;
import org.slf4j.Logger;

@RestController
@RequestMapping("/camData")
public class ControllerCam {
    private static final Logger log = LoggerFactory.getLogger(ControllerCam.class);
    @Value("${important}")
    private int IMPORTANT;
    @Value("${normal}")
    private int NORMAL;
    @Value("${urgent}")
    private int URGENT;
    @Autowired
    private QueueService queueService;

    @PostMapping("/")
    public CompletableFuture<Object> receiveCamData(@RequestBody RequestCamDTO data) {
        log.info("Received and transfer cam data");
        return queueService.addRequestToQueue(URGENT, data, "receiveCamData");
    }

    @GetMapping("/{deviceId}")
    public CompletableFuture<Object> getCamData(@PathVariable String deviceId) {
        log.info("Received and transfer request for cam data of device");
        return queueService.addRequestToQueue(IMPORTANT, deviceId, "getCamData");
    }

    @GetMapping("/")
    public CompletableFuture<Object> getAllCamData() {
        log.info("Received and transfer request for all cam data");
        return queueService.addRequestToQueue(IMPORTANT, null, "getAllCamData");
    }
}
