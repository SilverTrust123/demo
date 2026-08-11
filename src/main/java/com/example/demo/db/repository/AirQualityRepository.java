package com.example.demo.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.db.entity.AirQuality;
import com.example.demo.db.entity.AirQualityId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AirQualityRepository extends JpaRepository<AirQuality, AirQualityId> {
        List<AirQuality> findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(
                        String deviceId, int startTime, int endTime);

        List<AirQuality> findByTimestampBetweenOrderByTimestampDesc(
                        int startTime, int endTime);

        List<AirQuality> findAllByOrderByTimestampDesc();

        @Transactional
        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("DELETE FROM AirQuality")
        void deleteAllAirQuality();
}
