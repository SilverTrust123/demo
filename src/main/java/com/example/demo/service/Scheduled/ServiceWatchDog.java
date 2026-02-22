package com.example.demo.service.Scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.service.ServiceDeviceState;

@Component
public class ServiceWatchDog {

    @Autowired
    private ServiceDeviceState deviceStatusService;

    @Scheduled(fixedRate = 3000)
    public void checkAllDevices() {
        deviceStatusService.markOfflineIfTimeout();
    }
}