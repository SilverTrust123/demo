package com.example.demo.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.responseDTO.*;

import org.slf4j.Logger;

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
    @Autowired
    private ServiceDeviceState serviceDeviceState;

    private static final Logger log = LoggerFactory.getLogger(ServiceData.class);

    public ResponseAllDataDTO AllData() throws Exception {
        ResponseAllDataDTO ans = new ResponseAllDataDTO(
                serviceTemparatureAndHumidity.getAllTemparatureAndHumidityData(), serviceCircuit.getAllCircuitData(),
                serviceAirQuality.getAllAirQualityData(), serviceAirParticulates.getAllAirParticalData(),
                serviceCam.getAllCamData(), servicePLC.AllDPointData(), servicePLC.AllMPointData());
        log.info("received all data request return {}", ans);
        return ans;
    }

    public ResponseAllSensorDataDTO AllSensorData() throws Exception {
        ResponseAllSensorDataDTO ans = new ResponseAllSensorDataDTO(
                serviceTemparatureAndHumidity.getAllTemparatureAndHumidityData(), serviceCircuit.getAllCircuitData(),
                serviceAirQuality.getAllAirQualityData(), serviceAirParticulates.getAllAirParticalData(),
                serviceCam.getAllCamData(), serviceDeviceState.getAllDeviceStatuses());
        log.info("received all data request return {}", ans);
        return ans;
    }

    public ResponseAllDataAndDeviceStateDTO AllDataAndDeviceState() throws Exception {
        ResponseAllDataAndDeviceStateDTO ans = new ResponseAllDataAndDeviceStateDTO(
                serviceTemparatureAndHumidity.getAllTemparatureAndHumidityData(), serviceCircuit.getAllCircuitData(),
                serviceAirQuality.getAllAirQualityData(), serviceAirParticulates.getAllAirParticalData(),
                serviceCam.getAllCamData(), servicePLC.AllDPointData(), servicePLC.AllMPointData(),
                serviceDeviceState.getAllDeviceStatuses());
        log.info("received all data and device state request return {}", ans);
        return ans;
    }

    public ResponseAllDataAndDeviceStateWithoutPLCDTO AllDataAndDeviceStateWithoutPLC() throws Exception {
        ResponseAllDataAndDeviceStateWithoutPLCDTO ans = new ResponseAllDataAndDeviceStateWithoutPLCDTO(
                serviceTemparatureAndHumidity.getAllTemparatureAndHumidityData(), serviceCircuit.getAllCircuitData(),
                serviceAirQuality.getAllAirQualityData(), serviceAirParticulates.getAllAirParticalData(),
                serviceCam.getAllCamData(),
                serviceDeviceState.getAllDeviceStatuses());
        log.info("received all data and device state request return {}", ans);
        return ans;
    }
}

// magicData.put("temperatureAndHumidityDataMap",
// serviceTemparatureAndHumidity.getAllTemparatureAndHumidityData());
// magicData.put("circuitDataMap", serviceCircuit.getAllCircuitData());
// magicData.put("airQualityDataMap", serviceAirQuality.getAllAirQualityData());
// magicData.put("airParticulatesDataMap",
// serviceAirParticulates.getAllAirParticalData());
// magicData.put("camDataMap", serviceCam.getAllCamData());
// magicData.put("DPoint", servicePLC.AllDPointData());
// magicData.put("MPoint", servicePLC.AllMPointData());