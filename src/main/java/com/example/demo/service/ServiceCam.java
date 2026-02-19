package com.example.demo.service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.requestDTO.*;
import com.example.demo.DTO.responseDTO.*;

import org.slf4j.Logger;

@Service
public class ServiceCam {
    private static final Logger log = LoggerFactory.getLogger(ServiceCam.class);
    private Map<String, ResponseCamDTO> camDataMap = new ConcurrentHashMap<>();

    public String receiveCamData(RequestCamDTO request) {
        String deviceId = request.getDeviceId();
        if (deviceId == null) {
            log.info("received cam data from {} ", deviceId);
            return "Cam deviceId is required";
        }
        int timestamp = (int) (System.currentTimeMillis() / 1000L);
        ResponseCamDTO finalData = new ResponseCamDTO(
                deviceId,
                request.isDanger(),
                request.getPersonCount(),
                request.getDangerZone(),
                request.getObjects(),
                timestamp

        );
        camDataMap.put(deviceId, finalData);
        log.info("{} put in ok", deviceId);
        return "OK";
    }

    public ResponseCamDTO getCamData(String deviceId) {
        ResponseCamDTO ans = camDataMap.get(deviceId);
        if (ans != null && isTimeValid(ans)) {
            log.info("Received request single device {} and return detail {}", deviceId, ans);
            return ans;
        }
        log.warn("Cannot find valid data for device name {}", deviceId);
        return new ResponseCamDTO(deviceId, false, 0, null, null, 0);
    }

    public Collection<ResponseCamDTO> getAllCamData() {
        Collection<ResponseCamDTO> ans = camDataMap.values()
                .stream()
                .filter(this::isTimeValid)
                .toList();
        log.info("received all cam data request detail {} ", ans);
        return ans;
    }

    private boolean isTimeValid(ResponseCamDTO data) {
        if (data == null) {
            return false;
        }
        int now = (int) (System.currentTimeMillis() / 1000L);
        int gap = now - data.timestamp();

        if (gap > 60) {
            log.warn("Data of device {} is too old, timestamp {}, now {}",
                    data.deviceId(), data.timestamp(), now);
            return false;
        }
        return true;
    }
}
