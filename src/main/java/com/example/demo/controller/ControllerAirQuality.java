package com.example.demo.controller;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.sensor.SensorDataAirQuality;
import com.example.demo.service.ServiceAirQuality;

@RestController
@RequestMapping("/airQuality")
public class ControllerAirQuality {
    private static final Logger log = LoggerFactory.getLogger(ControllerTemparatureAndHumidity.class);
    @Autowired
    private ServiceAirQuality serviceAirQuality;

    @PostMapping("/AirQualityData")
    public String recriveAirQuality(@RequestBody SensorDataAirQuality data) {
        log.info("Received and transfer air quality data");
        return serviceAirQuality.recriveAirQuality(data);
    }

    @GetMapping("/AirQualityData/{deviceId}")
    public SensorDataAirQuality getAirQualityData(@PathVariable String deviceId) {
        log.info("Received and transfer request for air quality data of device");
        return serviceAirQuality.getAirQualityData(deviceId);
    }

    @GetMapping("/AirQualityData")
    public Collection<SensorDataAirQuality> getAllAirQualityData() {
        log.info("Received and transfer request for all air quality data");
        return serviceAirQuality.getAllAirQualityData();
    }
}
