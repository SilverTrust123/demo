package com.example.demo.service;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.sensor.SensorDataAirQuality;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class ServiceAirQuality {
    private Map<String, SensorDataAirQuality> airQualityDataMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Deque<Double>> airQualityHistoryMap = new ConcurrentHashMap<>();
    Dotenv dotenv = Dotenv.load();
    private final int WINDOW_SIZE = Integer.parseInt(dotenv.get("WINDOW_SIZE"));
    private static final Logger log = LoggerFactory.getLogger(ServiceAirQuality.class);

    public String recriveAirQuality(@RequestBody SensorDataAirQuality data) {
        if (data.getDeviceId() == null || data.getDeviceId().isEmpty()) {
            log.info("air quality deviceId is required detaild by follow {} ", data);
            return "air quality deviceId is required";
        }
        data.setTimestamp((int) (System.currentTimeMillis() / 1000L));
        String deviceId = data.getDeviceId();
        float rawAQ = data.getAirPollution();
        float smoothAQ = calculateAverage(airQualityHistoryMap, deviceId, rawAQ);
        data.setAirPollution(smoothAQ);
        airQualityDataMap.put(deviceId, data);
        log.info("{} put in ok", deviceId);
        return "OK";
    }

    public SensorDataAirQuality getAirQualityData(@PathVariable String deviceId) {
        SensorDataAirQuality ans = airQualityDataMap.get(deviceId);
        if (ans != null && isTimeValid(ans)) {
            log.info("Received request single device {} and return detail {}", deviceId, ans);
            return ans;
        }
        log.warn("Cannot find valid data for device name {}", deviceId);
        return new SensorDataAirQuality();
    }

    public Collection<SensorDataAirQuality> getAllAirQualityData() {
        Collection<SensorDataAirQuality> ans = airQualityDataMap.values()
                .stream()
                .filter(this::isTimeValid)
                .toList();
        log.info("received all air quality data request detail {} ", ans);
        return ans;
    }

    private boolean isTimeValid(SensorDataAirQuality data) {
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
