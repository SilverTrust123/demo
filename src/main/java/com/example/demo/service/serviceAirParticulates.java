package com.example.demo.service;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.requestDTO.RequestAirParticulatesDTO;
import com.example.demo.DTO.responseDTO.*;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class ServiceAirParticulates {

    private Map<String, ResponseAirParticulatesDTO> airParticulatesDataMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Deque<Double>> airParticulatesHistoryMap = new ConcurrentHashMap<>();
    Dotenv dotenv = Dotenv.load();
    private final int WINDOW_SIZE = Integer.parseInt(dotenv.get("WINDOW_SIZE"));
    private static final Logger log = LoggerFactory.getLogger(ServiceAirParticulates.class);

    public String recriveAirPartical(RequestAirParticulatesDTO request) {
        if (request.getDeviceId() == null || request.getDeviceId().isEmpty()) {
            log.info("air particulates deviceId is required detaild by follow {} ", request);
            return "air particulates deviceId is required";
        }
        String deviceId = request.getDeviceId();
        float rawAP = request.getPm2_5();
        int currentTimestamp = (int) (System.currentTimeMillis() / 1000L);
        float smoothAP = calculateAverage(airParticulatesHistoryMap, deviceId, (double) rawAP);
        ResponseAirParticulatesDTO finalData = new ResponseAirParticulatesDTO(
                deviceId,
                smoothAP,
                currentTimestamp);
        airParticulatesDataMap.put(deviceId, finalData);
        log.info("{} put in ok", deviceId);
        return "OK";
    }

    public ResponseAirParticulatesDTO getAirParticalData(String deviceId) {
        ResponseAirParticulatesDTO ans = airParticulatesDataMap.get(deviceId);
        if (ans != null && isTimeValid(ans)) {
            log.info("Received request single device {} and return detail {}", deviceId, ans);
            return ans;
        }
        log.warn("Cannot find valid data for device name {}", deviceId);
        return new ResponseAirParticulatesDTO(deviceId, 0.0f, 0);
    }

    public Collection<ResponseAirParticulatesDTO> getAllAirParticalData() {
        Collection<ResponseAirParticulatesDTO> ans = airParticulatesDataMap.values()
                .stream()
                .filter(this::isTimeValid)
                .toList();
        log.info("received all air particulates data request detail {} ", ans);
        return ans;
    }

    private float calculateAverage(ConcurrentHashMap<String, Deque<Double>> historyMap, String deviceId,
            double newValue) {
        Deque<Double> window = historyMap.computeIfAbsent(deviceId, k -> new ArrayDeque<>());

        synchronized (window) {
            if (window.size() >= WINDOW_SIZE) {
                window.pollFirst();
            }
            window.addLast(newValue);
            log.info("Device {} new value: {}, history: {}", deviceId, newValue, window);
            return (float) window.stream().mapToDouble(Double::doubleValue).average().orElse(newValue);
        }
    }

    private boolean isTimeValid(ResponseAirParticulatesDTO data) {
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
