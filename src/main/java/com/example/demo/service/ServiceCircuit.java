package com.example.demo.service;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import com.example.demo.DTO.requestDTO.RequestCircuitDTO;
import com.example.demo.DTO.responseDTO.ResponseCircuitDTO;

import io.github.cdimascio.dotenv.Dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ServiceCircuit {
    private Map<String, ResponseCircuitDTO> circuitDataMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Deque<Double>> circuitHistoryMap = new ConcurrentHashMap<>();
    Dotenv dotenv = Dotenv.load();
    private final int WINDOW_SIZE = Integer.parseInt(dotenv.get("WINDOW_SIZE"));
    private static final Logger log = LoggerFactory.getLogger(ServiceCircuit.class);

    public String receiveCircuitData(RequestCircuitDTO request) {
        if (request.getDeviceId() == null || request.getDeviceId().isEmpty()) {
            log.info("circuit data is required follow by detail", request);
            return "circuit deviceId is required";
        }

        String deviceId = request.getDeviceId();
        float rawVoltage = request.getVoltage();
        float smoothVoltage = calculateAverage(circuitHistoryMap, deviceId, (double) rawVoltage);
        float rawCurrent = request.getCurrent();
        float smoothCurrent = calculateAverage(circuitHistoryMap, deviceId, rawCurrent);
        float rawPower = request.getPower();
        float smoothPower = calculateAverage(circuitHistoryMap, deviceId, rawPower);
        float rawEnergy = request.getEnergy();
        float smoothEnergy = calculateAverage(circuitHistoryMap, deviceId, rawEnergy);
        int currentTimestamp = (int) (System.currentTimeMillis() / 1000L);
        ResponseCircuitDTO finalData = new ResponseCircuitDTO(
                deviceId,
                smoothVoltage,
                smoothCurrent,
                smoothPower,
                smoothEnergy,
                currentTimestamp);

        circuitDataMap.put(deviceId, finalData);

        log.info("{} put in ok", deviceId);
        return "OK";
    }

    public ResponseCircuitDTO getCircuitData(String deviceId) {
        ResponseCircuitDTO ans = circuitDataMap.get(deviceId);
        if (ans != null && isTimeValid(ans)) {
            log.info("Received request single device {} and return detail {}", deviceId, ans);
            return ans;
        }
        log.warn("Cannot find valid data for device name {}", deviceId);
        return new ResponseCircuitDTO(deviceId, 0f, 0f, 0f, 0f, 0);
    }

    public Collection<ResponseCircuitDTO> getAllCircuitData() {
        Collection<ResponseCircuitDTO> ans = circuitDataMap.values()
                .stream()
                .filter(this::isTimeValid)
                .toList();
        log.info("received all circuit data request detail {} ", ans);
        return ans;
    }

    private boolean isTimeValid(ResponseCircuitDTO data) {
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
}
