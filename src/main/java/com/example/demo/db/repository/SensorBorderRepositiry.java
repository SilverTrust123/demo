package com.example.demo.db.repository;

import com.example.demo.db.entity.SensorBorder;
import com.example.demo.db.entity.SensorBorderId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SensorBorderRepositiry extends JpaRepository<SensorBorder, SensorBorderId> {

        Optional<SensorBorder> findFirstBySensorGroupOrderByTimestampDesc(String sensorGroup);

        List<SensorBorder> findBySensorGroupAndTimestampBetweenOrderByTimestampAsc(
                        String sensorGroup, int startTime, int endTime);
}