package com.example.demo.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.db.entity.Circuit;
import com.example.demo.db.entity.CircuitId;

import java.util.List;

@Repository
public interface CircuitRepository extends JpaRepository<Circuit, CircuitId> {
    List<Circuit> findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(
            String deviceId, int startTime, int endTime);

    List<Circuit> findAllByOrderByTimestampDesc();
}
