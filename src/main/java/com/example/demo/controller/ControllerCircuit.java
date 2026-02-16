package com.example.demo.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.sensor.SensorDataCircuit;
import com.example.demo.service.ServiceCircuit;

import java.util.Collection;

import org.slf4j.Logger;

@RestController
@RequestMapping("/circuit")
public class ControllerCircuit {
    @Autowired
    private ServiceCircuit serviceCircuit;
    private static final Logger log = LoggerFactory.getLogger(ControllerCircuit.class);

    @PostMapping("/CircuitData")
    public String receiveCircuitData(@RequestBody SensorDataCircuit data) {
        log.info("Received and transfer circuit data");
        return serviceCircuit.receiveCircuitData(data);
    }

    // 船空的回去就是找不到東西 或是太舊了
    @GetMapping("/CircuitData/{deviceId}")
    public SensorDataCircuit getCircuitData(@PathVariable String deviceId) {
        log.info("Received and transfer request for circuit data of device");
        return serviceCircuit.getCircuitData(deviceId);
    }

    // 這個本來最多就一個 如果船空的回去就是太舊了
    @GetMapping("/CircuitData")
    public Collection<SensorDataCircuit> getAllCircuitData() {
        log.info("Received and transfer request for all circuit data");
        return serviceCircuit.getAllCircuitData();
    }
}
