package com.example.demo.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.db.entity.SensorBorder;
import com.example.demo.db.entity.SensorBorderId;

import java.util.List;

public interface SensorBorderRepositiry extends JpaRepository<SensorBorder, SensorBorderId> {
        List<AirQuality> findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(
                        String deviceId, int startTime, int endTime);

        List<AirQuality> findByTimestampBetweenOrderByTimestampDesc(
                        int startTime, int endTime);

        List<AirQuality> findAllByOrderByTimestampDesc();

}
