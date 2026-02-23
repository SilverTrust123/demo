package com.example.demo.service.history;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.responseDTO.history.ResponseHistoryAirParticulatesDTO;
import com.example.demo.DTO.responseDTO.*;
import com.example.demo.db.repository.AirParticulatesRepository;

@Service
public class ServiceHistoryAirParticulates {
    @Autowired
    private AirParticulatesRepository apRepo;

    public ResponseHistoryAirParticulatesDTO getAirParticulatesHistory(String deviceId, int start, int end) {
        return new ResponseHistoryAirParticulatesDTO(
                apRepo.findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(deviceId, start, end)
                        .stream()
                        .map(entity -> new ResponseAirParticulatesDTO(
                                entity.getDeviceId(),
                                entity.getPm2_5(),
                                entity.getTimestamp()))
                        .collect(Collectors.toList()));
    }
}
