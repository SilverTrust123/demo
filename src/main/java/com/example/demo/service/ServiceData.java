package com.example.demo.service;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

@Service
public class ServiceData {

    @Autowired
    private ServiceTemparatureAndHumidity serviceTemparatureAndHumidity;
    @Autowired
    private ServiceCircuit serviceCircuit;
    @Autowired
    private ServiceAirQuality serviceAirQuality;
    @Autowired
    private ServiceAirParticulates serviceAirParticulates;
    @Autowired
    private ServiceCam serviceCam;
    @Autowired
    private ServicePLC servicePLC;

    private ConcurrentHashMap<String, Object> magicData = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(ServiceData.class);

    @GetMapping("/AllData")
    public ConcurrentHashMap<String, Object> AllData(@RequestParam String param) throws Exception {
        magicData.put("temperatureAndHumidityDataMap",
                serviceTemparatureAndHumidity.getAllTemparatureAndHumidityData());
        magicData.put("circuitDataMap", serviceCircuit.getAllCircuitData());
        magicData.put("airQualityDataMap", serviceAirQuality.getAllAirQualityData());
        magicData.put("airParticulatesDataMap", serviceAirParticulates.getAllAirParticalData());
        magicData.put("camDataMap", serviceCam.getAllCamData());
        magicData.put("DPoint", servicePLC.AllDPointData());
        magicData.put("MPoint", servicePLC.AllMPointData());
        log.info("received all data request with param {} and return {}", param, magicData);
        return magicData;
    }
}
