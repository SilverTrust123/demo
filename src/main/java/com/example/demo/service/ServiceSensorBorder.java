package com.example.demo.service;

import com.example.demo.db.entity.SensorBorder;
import com.example.demo.db.repository.SensorBorderRepositiry;
import com.example.demo.DTO.requestDTO.*;
import com.example.demo.DTO.responseDTO.ResponseSensorBorderDTO;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceSensorBorder {

    private final SensorBorderRepositiry repository;

    public ServiceSensorBorder(SensorBorderRepositiry repository) {
        this.repository = repository;
    }

    public ResponseSensorBorderDTO saveOrUpdate(RequestSensorBorderDTO req) {
        SensorBorder entity = new SensorBorder();
        entity.setSensor_group(req.sensor_group());
        entity.setTemp_1(req.temp_1());
        entity.setTemp_2(req.temp_2());
        entity.setHumi_1(req.humi_1());
        entity.setHumi_2(req.humi_2());
        entity.setDust(req.dust());
        entity.setQua(req.qua());
        entity.setPow(req.pow());

        int currentTimestamp = (int) (System.currentTimeMillis() / 1000);
        entity.setTimestamp(currentTimestamp);

        SensorBorder savedEntity = repository.save(entity);
        return convertToResponseDTO(savedEntity);
    }

    public Optional<ResponseSensorBorderDTO> getBySensorGroup() {
        return repository.findFirstBySensorGroupOrderByTimestampDesc("A")
                .map(this::convertToResponseDTO);
    }

    private ResponseSensorBorderDTO convertToResponseDTO(SensorBorder entity) {
        return new ResponseSensorBorderDTO(
                entity.getSensor_group(),
                entity.getTemp_1(),
                entity.getTemp_2(),
                entity.getHumi_1(),
                entity.getHumi_2(),
                entity.getDust(),
                entity.getQua(),
                entity.getPow());
    }

}