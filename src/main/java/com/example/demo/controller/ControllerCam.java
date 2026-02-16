package com.example.demo.controller;

import java.util.Collection;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.cam.SensorDataCam;
import com.example.demo.service.ServiceCam;
import org.slf4j.Logger;

@RestController
@RequestMapping("/cam")
public class ControllerCam {
    private static final Logger log = LoggerFactory.getLogger(ControllerCam.class);

    @Autowired
    private ServiceCam serviceCam;

    @PostMapping("/CamData")
    public String receiveCamData(@RequestBody SensorDataCam data) {
        log.info("Received and transfer cam data");
        return serviceCam.receiveCamData(data);
    }

    @GetMapping("/CamData/{deviceId}")
    public SensorDataCam getCamData(@PathVariable String deviceId) {
        log.info("Received and transfer request for cam data of device");
        return serviceCam.getCamData(deviceId);
    }

    @GetMapping("/CamData")
    public Collection<SensorDataCam> getAllCamData() {
        log.info("Received and transfer request for all cam data");
        return serviceCam.getAllCamData();
    }
}
