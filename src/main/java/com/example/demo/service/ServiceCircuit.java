package com.example.demo.service;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.sensor.SensorDataCircuit;

import io.github.cdimascio.dotenv.Dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ServiceCircuit {
    private Map<String, SensorDataCircuit> circuitDataMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Deque<Double>> circuitHistoryMap = new ConcurrentHashMap<>();
    Dotenv dotenv = Dotenv.load();
    private final int WINDOW_SIZE = Integer.parseInt(dotenv.get("WINDOW_SIZE"));
    private static final Logger log = LoggerFactory.getLogger(ServiceCircuit.class);

    public String receiveCircuitData(@RequestBody SensorDataCircuit data) {
        if (data.getDeviceId() == null || data.getDeviceId().isEmpty()) {
            log.info("circuit data is required follow by detail", data);
            return "circuit deviceId is required";
        }
        // historyMap, String deviceId,double newValue
        data.setTimestamp((int) (System.currentTimeMillis() / 1000L));
        String deviceId = data.getDeviceId();
        float rawVol = data.getVoltage();
        float smoothVol = calculateAverage(circuitHistoryMap, deviceId, (double) rawVol);
        data.setVoltage(smoothVol);
        circuitDataMap.put(deviceId, data);
        log.info("{}put in ok", deviceId);
        return "OK";
    }

    public SensorDataCircuit getCircuitData(@PathVariable String deviceId) {
        SensorDataCircuit ans = circuitDataMap.get(deviceId);
        if (ans != null && isTimeValid(ans)) {
            log.info("Received request single device {} and return detail {}", deviceId, ans);
            return ans;
        }
        log.warn("Cannot find valid data for device name {}", deviceId);
        return new SensorDataCircuit();
    }

    public Collection<SensorDataCircuit> getAllCircuitData() {
        Collection<SensorDataCircuit> ans = circuitDataMap.values()
                .stream()
                .filter(this::isTimeValid)
                .toList();
        log.info("received all circuit data request detail {} ", ans);
        return ans;
    }

    private boolean isTimeValid(SensorDataCircuit data) {
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
