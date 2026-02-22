package com.example.demo.service.history;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.responseDTO.ResponseCircuitDTO;
import com.example.demo.DTO.responseDTO.history.ResponseHistoryCircuitDTO;
import com.example.demo.db.repository.*;

@Service
public class ServiceHistoryCircuit {
    @Autowired
    private CircuitRepository circuitRepo;

    public ResponseHistoryCircuitDTO getCircuitHistory(String deviceId, int start, int end) {
        return new ResponseHistoryCircuitDTO(
                circuitRepo.findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(deviceId, start, end)
                        .stream()
                        .map(entity -> new ResponseCircuitDTO(
                                entity.getDeviceId(),
                                entity.getVoltage(),
                                entity.getCurrent(),
                                entity.getPower(),
                                entity.getEnergy(),
                                entity.getTimestamp()))
                        .collect(Collectors.toList()));
    }

}
