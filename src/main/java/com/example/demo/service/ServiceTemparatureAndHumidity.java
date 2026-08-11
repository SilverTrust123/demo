package com.example.demo.service;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.DTO.requestDTO.RequestTemperatureAndHumidityDTO;
import com.example.demo.DTO.responseDTO.ResponseTemperatureAndHumidityDTO;
import com.example.demo.db.repository.TemperatureAndHumidityRepository;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class ServiceTemparatureAndHumidity {
    @Autowired
    private ServiceLog serviceLog;
    @Autowired
    private TemperatureAndHumidityRepository tempRepo;

    private Map<String, ResponseTemperatureAndHumidityDTO> temperatureAndHumidityDataMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Deque<Double>> tempHistoryMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Deque<Double>> humidHistoryMap = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(ServiceTemparatureAndHumidity.class);
    Dotenv dotenv = Dotenv.load();
    private final int WINDOW_SIZE = Integer.parseInt(dotenv.get("WINDOW_SIZE"));

    public String receiveTemparatureAndHumidityData(RequestTemperatureAndHumidityDTO request) {
        if (request.getDeviceId() == null || request.getDeviceId().isEmpty()) {
            log.info("Temparature and humidity deviceId is required follow by detail {}", request);
            return "Temparature and humidity deviceId is required";
        }
        String deviceId = request.getDeviceId();
        int timestamp = (int) (System.currentTimeMillis() / 1000L);
        float rawTemparature = request.getTemperature();
        float smoothTemparature = calculateAverage(tempHistoryMap, deviceId, rawTemparature);
        float rawHumidity = request.getHumidity();
        float smoothHumidity = calculateAverage(humidHistoryMap, deviceId, rawHumidity);
        ResponseTemperatureAndHumidityDTO finalData = new ResponseTemperatureAndHumidityDTO(
                deviceId,
                smoothTemparature,
                smoothHumidity,
                timestamp);

        temperatureAndHumidityDataMap.put(deviceId, finalData);
        log.info("{}put in ok", deviceId);

        return "OK";
    }

    public ResponseTemperatureAndHumidityDTO getTemparatureAndHumidityData(String deviceId) {
        ResponseTemperatureAndHumidityDTO ans = temperatureAndHumidityDataMap.get(deviceId);
        if (ans != null && isTimeValid(ans)) {
            log.info("Received request single device {} and return detail {}", deviceId, ans);
            return ans;
        }
        log.warn("Cannot find valid data for device name {}", deviceId);
        serviceLog.record("WARN", "ServiceTemparatureAndHumidity",
                "Cannot find valid data for device name " + deviceId);
        return new ResponseTemperatureAndHumidityDTO(deviceId, 0, 0, 0);

    }

    public Collection<ResponseTemperatureAndHumidityDTO> getAllTemparatureAndHumidityData() {
        Collection<ResponseTemperatureAndHumidityDTO> ans = temperatureAndHumidityDataMap.values()
                .stream()
                .filter(this::isTimeValid)
                .toList();
        log.info("received all temperature and humidity data request detail {} ", ans);
        return ans;
    }

    public String truncateTemperatureAndHumidity() {
        tempRepo.deleteAllTemperatureAndHumidity();
        log.info("secess truncate the temp n hudm data ");
        return new String("secces");
    }

    private boolean isTimeValid(ResponseTemperatureAndHumidityDTO data) {
        if (data == null) {
            return false;
        }
        int now = (int) (System.currentTimeMillis() / 1000L);
        int gap = now - data.timestamp();

        if (gap > 60) {
            log.warn("Data of device {} is too old, timestamp {}, now {}",
                    data.deviceId(), data.timestamp(), now);
            serviceLog.record("WARN", "ServiceTemparatureAndHumidity", "Data of device " + data.deviceId()
                    + " is too old, timestamp " + data.timestamp() + ", now " + now);
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
