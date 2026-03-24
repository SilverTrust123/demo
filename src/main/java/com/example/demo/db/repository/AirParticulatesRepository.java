package com.example.demo.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.db.entity.*;
import java.util.List;

public interface AirParticulatesRepository extends JpaRepository<AirParticulates, AirParticulatesId> {
    List<AirParticulates> findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(
            String deviceId, int startTime, int endTime);

    List<AirParticulates> findByTimestampBetweenOrderByTimestampDesc(
            int startTime, int endTime);

    List<AirParticulates> findAllByOrderByTimestampDesc();
}