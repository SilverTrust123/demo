package com.example.demo.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.db.entity.AirQuality;
import com.example.demo.db.entity.AirQualityId;

import java.util.List;

public interface AirQualityRepository extends JpaRepository<AirQuality, AirQualityId> {
    List<AirQuality> findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(
            String deviceId, int startTime, int endTime);

    List<AirQuality> findByTimestampBetweenOrderByTimestampDesc(
            int startTime, int endTime);

    List<AirQuality> findAllByOrderByTimestampDesc();
}