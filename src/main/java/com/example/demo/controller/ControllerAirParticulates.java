package com.example.demo.controller;

import java.util.Collection;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.sensor.SensorDataAirParticulates;
import com.example.demo.service.ServiceAirParticulates;
import org.slf4j.Logger;

public class ControllerAirParticulates {
    @Autowired
    private ServiceAirParticulates serviceAirParticulates;
    private static final Logger log = LoggerFactory.getLogger(ControllerAirParticulates.class);

    @PostMapping("/AirParticalData")
    public String recriveAirPartical(@RequestBody SensorDataAirParticulates data) {
        log.info("Received and transfer air particulates data");
        return serviceAirParticulates.recriveAirPartical(data);
    }

    @GetMapping("/AirParticalData/{deviceId}")
    public SensorDataAirParticulates getAirParticalData(@PathVariable String deviceId) {
        log.info("Received and transfer request for air particulates data of device");
        return serviceAirParticulates.getAirParticalData(deviceId);
    }

    @GetMapping("/AirParticalData")
    public Collection<SensorDataAirParticulates> getAllAirParticalData() {
        log.info("Received and transfer request for all air particulates data");
        return serviceAirParticulates.getAllAirParticalData();
    }
}
