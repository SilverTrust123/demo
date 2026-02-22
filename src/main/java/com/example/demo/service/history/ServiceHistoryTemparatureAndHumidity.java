package com.example.demo.service.history;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.DTO.responseDTO.ResponseTemperatureAndHumidityDTO;
import com.example.demo.db.repository.TemperatureAndHumidityRepository;
import org.springframework.stereotype.Service;
import com.example.demo.DTO.responseDTO.history.*;

@Service
public class ServiceHistoryTemparatureAndHumidity {
    @Autowired
    private TemperatureAndHumidityRepository tempRepo;

    public ResponseHistoryTemparatureAndHumidityDTO getTempHistory(String deviceId, int start, int end) {
        return new ResponseHistoryTemparatureAndHumidityDTO(
                tempRepo.findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(deviceId, start, end)
                        .stream()
                        .map(entity -> new ResponseTemperatureAndHumidityDTO(
                                entity.getDeviceId(),
                                entity.getTemperature(),
                                entity.getHumidity(),
                                entity.getTimestamp()))
                        .collect(Collectors.toList()));
    }
}
