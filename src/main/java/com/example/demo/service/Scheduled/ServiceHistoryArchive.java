package com.example.demo.service.Scheduled;

import com.example.demo.DTO.responseDTO.ResponseTemperatureAndHumidityDTO;
import com.example.demo.db.entity.TemperatureAndHumidity;
import com.example.demo.db.repository.TemperatureAndHumidityRepository;
import com.example.demo.service.ServiceTemparatureAndHumidity;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import org.slf4j.Logger;

@Service
public class ServiceHistoryArchive {
    private static final Logger log = LoggerFactory.getLogger(ServiceHistoryArchive.class);

    @Autowired
    private ServiceTemparatureAndHumidity serviceTemparatureAndHumidity; // 抄數據的地方

    @Autowired
    private TemperatureAndHumidityRepository tempRepo; // 存檔的地方

    @Scheduled(fixedRate = 300000)
    public void archiveTempData() {

        // 1. 從大廚那邊拿到目前最新的 Collection
        Collection<ResponseTemperatureAndHumidityDTO> currentData = serviceTemparatureAndHumidity
                .getAllTemparatureAndHumidityData();

        // 如果目前沒半台設備有資料，就直接下班休息
        if (currentData == null || currentData.isEmpty()) {
            log.warn("no temperature and humidity data to archive, skip this round");
            return;
        }

        List<TemperatureAndHumidity> entitiesToSave = new ArrayList<>();

        // 2. 將 DTO 轉換為 Entity (準備入庫的包裹)
        for (ResponseTemperatureAndHumidityDTO dto : currentData) {
            TemperatureAndHumidity entity = new TemperatureAndHumidity();
            entity.setDeviceId(dto.deviceId());
            entity.setTemperature(dto.temperature());
            entity.setHumidity(dto.humidity());
            entity.setTimestamp(dto.timestamp());

            entitiesToSave.add(entity);
        }

        tempRepo.saveAll(entitiesToSave);
        log.info("temp n humid seces put in db");
    }
}