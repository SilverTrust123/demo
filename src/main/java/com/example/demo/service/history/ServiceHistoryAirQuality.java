package com.example.demo.service.history;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.responseDTO.ResponseAirQualityDTO;
import com.example.demo.DTO.responseDTO.history.ResponseHistoryAirQualityDTO;
import com.example.demo.db.repository.AirQualityRepository;

@Service
public class ServiceHistoryAirQuality {
    @Autowired
    private AirQualityRepository aqRepo;

    public ResponseHistoryAirQualityDTO getAirQualityHistory(String deviceId, int start, int end) {
        return new ResponseHistoryAirQualityDTO(
                aqRepo.findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(deviceId, start, end)
                        .stream()
                        .map(entity -> new ResponseAirQualityDTO(
                                entity.getDeviceId(),
                                entity.getAirPollution(),
                                entity.getTimestamp()))
                        .collect(Collectors.toList()));
    }
}
