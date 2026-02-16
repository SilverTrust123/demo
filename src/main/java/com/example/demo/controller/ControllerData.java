package com.example.demo.controller;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ServiceData;
import org.slf4j.Logger;

@RestController
@RequestMapping("/data")
public class ControllerData {
    private static final Logger log = LoggerFactory.getLogger(ControllerData.class);

    @Autowired
    private ServiceData serviceData;

    @GetMapping("/AllData")
    public ConcurrentHashMap<String, Object> AllData(@RequestParam String param) throws Exception {
        log.info("Received and transfer request for all data with param {}", param);
        return serviceData.AllData(param);
    }
}
