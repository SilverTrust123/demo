package com.example.demo.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.responseDTO.*;
import com.example.demo.DTO.responseDTO.PLCResponseDTO.*;

import java.util.HashMap;

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
        ResponseAllDataDTO ans;
        if (!servicePLC.isPlcConnected()) {

            ans = new ResponseAllDataDTO(
                    serviceTemparatureAndHumidity.getAllTemparatureAndHumidityData(),
                    serviceCircuit.getAllCircuitData(),
                    serviceAirQuality.getAllAirQualityData(), serviceAirParticulates.getAllAirParticalData(),
                    serviceCam.getAllCamData(), new ResponseAllDPointStateDTO(new HashMap<String, Integer>()),
                    new ResponseAllMPointStateDTO(new HashMap<>()));
        } else {

            ans = new ResponseAllDataDTO(
                    serviceTemparatureAndHumidity.getAllTemparatureAndHumidityData(),
                    serviceCircuit.getAllCircuitData(),
                    serviceAirQuality.getAllAirQualityData(), serviceAirParticulates.getAllAirParticalData(),
                    serviceCam.getAllCamData(), servicePLC.AllDPointData(), servicePLC.AllMPointData());
        }

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
        ResponseAllDataAndDeviceStateDTO ans;
        if (!servicePLC.isPlcConnected()) {

            ans = new ResponseAllDataAndDeviceStateDTO(
                    serviceTemparatureAndHumidity.getAllTemparatureAndHumidityData(),
                    serviceCircuit.getAllCircuitData(),
                    serviceAirQuality.getAllAirQualityData(), serviceAirParticulates.getAllAirParticalData(),
                    serviceCam.getAllCamData(), new ResponseAllDPointStateDTO(new HashMap<>()),
                    new ResponseAllMPointStateDTO(new HashMap<>()),
                    serviceDeviceState.getAllDeviceStatuses());
        } else {

            ans = new ResponseAllDataAndDeviceStateDTO(
                    serviceTemparatureAndHumidity.getAllTemparatureAndHumidityData(),
                    serviceCircuit.getAllCircuitData(),
                    serviceAirQuality.getAllAirQualityData(), serviceAirParticulates.getAllAirParticalData(),
                    serviceCam.getAllCamData(), servicePLC.AllDPointData(), servicePLC.AllMPointData(),
                    serviceDeviceState.getAllDeviceStatuses());
            log.info("received all data and device state request return {}", ans);
        }
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