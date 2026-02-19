package com.example.demo.service;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.requestDTO.RequestAirQualityDTO;
import com.example.demo.DTO.responseDTO.ResponseAirQualityDTO;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class ServiceAirQuality {
    private Map<String, ResponseAirQualityDTO> airQualityDataMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Deque<Double>> airQualityHistoryMap = new ConcurrentHashMap<>();
    Dotenv dotenv = Dotenv.load();
    private final int WINDOW_SIZE = Integer.parseInt(dotenv.get("WINDOW_SIZE"));
    private static final Logger log = LoggerFactory.getLogger(ServiceAirQuality.class);

    public String recriveAirQuality(RequestAirQualityDTO request) {
        if (request.getDeviceId() == null || request.getDeviceId().isEmpty()) {
            log.info("air quality deviceId is required detaild by follow {} ", request);
            return "air quality deviceId is required";
        }
        String deviceId = request.getDeviceId();
        float rawAQ = request.getAirPollution();
        float smoothAQ = calculateAverage(airQualityHistoryMap, deviceId, rawAQ);
        int currentTimestamp = (int) (System.currentTimeMillis() / 1000L);
        ResponseAirQualityDTO finalData = new ResponseAirQualityDTO(
                deviceId,
                smoothAQ,
                currentTimestamp);
        airQualityDataMap.put(deviceId, finalData);
        log.info("{} put in ok", deviceId);
        return "OK";
    }

    public ResponseAirQualityDTO getAirQualityData(String deviceId) {
        ResponseAirQualityDTO ans = airQualityDataMap.get(deviceId);
        if (ans != null && isTimeValid(ans)) {
            log.info("Received request single device {} and return detail {}", deviceId, ans);
            return ans;
        }
        log.warn("Cannot find valid data for device name {}", deviceId);
        return new ResponseAirQualityDTO(deviceId, 0f, 0);
    }

    public Collection<ResponseAirQualityDTO> getAllAirQualityData() {
        Collection<ResponseAirQualityDTO> ans = airQualityDataMap.values()
                .stream()
                .filter(this::isTimeValid)
                .toList();
        log.info("received all air quality data request detail {} ", ans);
        return ans;
    }

    private boolean isTimeValid(ResponseAirQualityDTO data) {
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
