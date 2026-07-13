package com.example.demo.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.db.entity.Circuit;
import com.example.demo.db.entity.CircuitId;

import java.util.List;

public interface CircuitRepository extends JpaRepository<Circuit, CircuitId> {
        List<Circuit> findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(
                        String deviceId, int startTime, int endTime);

        List<Circuit> findByTimestampBetweenOrderByTimestampDesc(
                        int startTime, int endTime);

        List<Circuit> findAllByOrderByTimestampDesc();
}
