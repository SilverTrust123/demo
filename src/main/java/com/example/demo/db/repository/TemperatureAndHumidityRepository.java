package com.example.demo.db.repository;

import com.example.demo.db.entity.TemperatureAndHumidity;
import com.example.demo.db.entity.TemperatureAndHumidityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TemperatureAndHumidityRepository
                extends JpaRepository<TemperatureAndHumidity, TemperatureAndHumidityId> {
        List<TemperatureAndHumidity> findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(
                        String deviceId, int startTime, int endTime);

        List<TemperatureAndHumidity> findAllByOrderByTimestampDesc();
}