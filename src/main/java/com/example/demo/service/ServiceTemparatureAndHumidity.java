package com.example.demo.service;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.sensor.SensorDataTemperatureAndHumidity;

@Service
public class ServiceTemparatureAndHumidity {

    private Map<String, SensorDataTemperatureAndHumidity> temperatureAndHumidityDataMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Deque<Double>> tempHistoryMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Deque<Double>> humidHistoryMap = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(ServiceTemparatureAndHumidity.class);
    private final int WINDOW_SIZE = 10;

    public String receiveTemparatureAndHumidityData(@RequestBody SensorDataTemperatureAndHumidity data) {
        if (data.getDeviceId() == null || data.getDeviceId().isEmpty()) {
            log.info("Temparature and humidity deviceId is required follow by detail", data);
            return "Temparature and humidity deviceId is required";
        }
        data.setTimestamp((int) (System.currentTimeMillis() / 1000L));
        String deviceId = data.getDeviceId();
        double rawTemp = data.getTemperature();
        float smoothTemp = calculateAverage(tempHistoryMap, deviceId, rawTemp);
        data.setTemperature(smoothTemp);
        double rawHumid = data.getHumidity();
        float smoothHumid = calculateAverage(humidHistoryMap, deviceId, rawHumid);
        data.setHumidity(smoothHumid);
        temperatureAndHumidityDataMap.put(deviceId, data);
        log.info("{}put in ok", deviceId);

        return "OK";
    }

    public SensorDataTemperatureAndHumidity getTemparatureAndHumidityData(@PathVariable String deviceId) {
        SensorDataTemperatureAndHumidity ans = temperatureAndHumidityDataMap.get(deviceId);
        if (ans != null && isTimeValid(ans)) {
            log.info("Received request single device {} and return detail {}", deviceId, ans);
            return ans;
        }
        log.warn("Cannot find valid data for device name {}", deviceId);
        return new SensorDataTemperatureAndHumidity();

    }

    public Collection<SensorDataTemperatureAndHumidity> getAllTemparatureAndHumidityData() {
        Collection<SensorDataTemperatureAndHumidity> ans = temperatureAndHumidityDataMap.values()
                .stream()
                .filter(this::isTimeValid)
                .toList();
        log.info("received all temperature and humidity data request detail {} ", ans);
        return ans;
    }

    private boolean isTimeValid(SensorDataTemperatureAndHumidity data) {
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

    // silaing window 平滑數據 就可以消除一些突發的雜訊阿之類的
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

    ConcurrentHashMap<String, Object> magicData = new ConcurrentHashMap<>();
}
