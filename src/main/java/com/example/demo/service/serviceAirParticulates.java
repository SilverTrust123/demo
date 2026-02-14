package com.example.demo.service;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.sensor.SensorDataAirParticulates;

public class serviceAirParticulates {

    private Map<String, SensorDataAirParticulates> airParticulatesDataMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Deque<Double>> airParticulatesHistoryMap = new ConcurrentHashMap<>();

    private final int WINDOW_SIZE = 10;
    private static final Logger log = LoggerFactory.getLogger(serviceAirParticulates.class);

    public String recriveAirPartical(@RequestBody SensorDataAirParticulates data) {
        if (data.getDeviceId() == null || data.getDeviceId().isEmpty()) {
            log.info("air particulates deviceId is required detaild by follow {} ", data);
            return "air particulates deviceId is required";
        }
        String deviceId = data.getDeviceId();
        float rawAP = data.getPm2_5();
        float smoothAP = calculateAverage(airParticulatesHistoryMap, deviceId, rawAP);
        data.setPm2_5(smoothAP);
        data.setTimestamp((int) (System.currentTimeMillis() / 1000L));
        airParticulatesDataMap.put(deviceId, data);
        log.info("{} put in ok", deviceId);
        return "OK";
    }

    public SensorDataAirParticulates getAirParticalData(@PathVariable String deviceId) {
        SensorDataAirParticulates ans = airParticulatesDataMap.get(deviceId);
        if (ans != null && isTimeValid(ans)) {
            log.info("Received request single device {} and return detail {}", deviceId, ans);
            return ans;
        }
        log.warn("Cannot find valid data for device name {}", deviceId);
        return new SensorDataAirParticulates();
    }

    public Collection<SensorDataAirParticulates> getAllAirParticalData() {
        Collection<SensorDataAirParticulates> ans = airParticulatesDataMap.values()
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

    private boolean isTimeValid(SensorDataAirParticulates data) {
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
