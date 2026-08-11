package com.example.demo.db.repository;

import com.example.demo.db.entity.TemperatureAndHumidity;
import com.example.demo.db.entity.TemperatureAndHumidityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TemperatureAndHumidityRepository
                extends JpaRepository<TemperatureAndHumidity, TemperatureAndHumidityId> {
        List<TemperatureAndHumidity> findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(
                        String deviceId, int startTime, int endTime);

        List<TemperatureAndHumidity> findByTimestampBetweenOrderByTimestampDesc(
                        int startTime, int endTime);

        List<TemperatureAndHumidity> findAllByOrderByTimestampDesc();

        @Transactional
        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("DELETE FROM TemperatureAndHumidity")
        void deleteAllTemperatureAndHumidity();
}