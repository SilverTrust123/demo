package com.example.demo.service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.slf4j.Logger;

import com.example.demo.cam.SensorDataCam;

@Service
public class ServiceCam {
    private static final Logger log = LoggerFactory.getLogger(ServiceCam.class);
    private Map<String, SensorDataCam> camDataMap = new ConcurrentHashMap<>();

    public String receiveCamData(@RequestBody SensorDataCam data) {
        String deviceId = data.getDeviceId();
        if (deviceId == null) {
            log.info("received cam data from {} ", deviceId);
            return "Cam deviceId is required";
        }
        data.setTimestamp((int) (System.currentTimeMillis() / 1000L));
        camDataMap.put(deviceId, data);
        log.info("{} put in ok", deviceId);
        return "OK";
    }

    public SensorDataCam getCamData(@PathVariable String deviceId) {
        SensorDataCam ans = camDataMap.get(deviceId);
        if (ans != null && isTimeValid(ans)) {
            log.info("Received request single device {} and return detail {}", deviceId, ans);
            return ans;
        }
        log.warn("Cannot find valid data for device name {}", deviceId);
        return new SensorDataCam();
    }

    public Collection<SensorDataCam> getAllCamData() {
        Collection<SensorDataCam> ans = camDataMap.values()
                .stream()
                .filter(this::isTimeValid)
                .toList();
        log.info("received all cam data request detail {} ", ans);
        return ans;
    }

    private boolean isTimeValid(SensorDataCam data) {
        if (data == null) {
            return false;
        }
        int now = (int) (System.currentTimeMillis() / 1000L);
        int gap = now - data.getTimestamp();

        if (gap > 60) {
            log.warn("Data of device {} is too old, timestamp {}, now {}",
                    data.getDeviceId(), data.getTimestamp(), now);
            return false;
        }
        return true;
    }
}
