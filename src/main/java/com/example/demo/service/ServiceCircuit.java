package com.example.demo.service;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.DTO.requestDTO.RequestCircuitDTO;
import com.example.demo.DTO.responseDTO.ResponseCircuitDTO;

import io.github.cdimascio.dotenv.Dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ServiceCircuit {
    private Map<String, ResponseCircuitDTO> circuitDataMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, DeviceHistory> circuitHistoryMap = new ConcurrentHashMap<>();
    Dotenv dotenv = Dotenv.load();
    private final int WINDOW_SIZE = Integer.parseInt(dotenv.get("WINDOW_SIZE"));
    private static final Logger log = LoggerFactory.getLogger(ServiceCircuit.class);

    @Autowired
    private ServiceLog serviceLog;

    public String receiveCircuitData(RequestCircuitDTO request) {
        if (request.getDeviceId() == null || request.getDeviceId().isEmpty()) {
            log.info("circuit data is required follow by detail {}", request);
            return "circuit deviceId is required";
        }

        String deviceId = request.getDeviceId();

        float rawVoltage = request.getVoltage();
        float smoothVoltage = calculateAverageForMetric(deviceId, Metric.VOLTAGE, rawVoltage);

        float rawCurrent = request.getCurrent();
        float smoothCurrent = calculateAverageForMetric(deviceId, Metric.CURRENT, rawCurrent);

        float rawPower = request.getPower();
        float smoothPower = calculateAverageForMetric(deviceId, Metric.POWER, rawPower);

        float rawEnergy = request.getEnergy();
        float smoothEnergy = calculateAverageForMetric(deviceId, Metric.ENERGY, rawEnergy);

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
        serviceLog.record("WARN", "ServiceCircuit", "Cannot find valid data for device name " + deviceId);
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
        int gap = now - data.timestamp();

        if (gap > 60) {
            log.warn("Data of device {} is too old, timestamp {}, now {}",
                    data.deviceId(), data.timestamp(), now);
            serviceLog.record("WARN", "ServiceCircuit", "Data of device " + data.deviceId()
                    + " is too old, timestamp " + data.timestamp() + ", now " + now);
            return false;
        }
        return true;
    }

    private enum Metric {
        VOLTAGE, CURRENT, POWER, ENERGY
    }

    private static class DeviceHistory {
        final Deque<Double> voltageHistory = new ArrayDeque<>();
        final Deque<Double> currentHistory = new ArrayDeque<>();
        final Deque<Double> powerHistory = new ArrayDeque<>();
        final Deque<Double> energyHistory = new ArrayDeque<>();
    }

    private float calculateAverageForMetric(String deviceId, Metric metric, double newValue) {
        DeviceHistory history = circuitHistoryMap.computeIfAbsent(deviceId, k -> new DeviceHistory());
        Deque<Double> window;
        switch (metric) {
            case VOLTAGE -> window = history.voltageHistory;
            case CURRENT -> window = history.currentHistory;
            case POWER -> window = history.powerHistory;
            case ENERGY -> window = history.energyHistory;
            default -> window = history.voltageHistory;
        }
        synchronized (window) {
            if (window.size() >= WINDOW_SIZE) {
                window.pollFirst();
            }
            window.addLast(newValue);
            log.info("Device {} metric {} new value: {}, history: {}", deviceId, metric, newValue, window);
            return (float) window.stream().mapToDouble(Double::doubleValue).average().orElse(newValue);
        }
    }
}