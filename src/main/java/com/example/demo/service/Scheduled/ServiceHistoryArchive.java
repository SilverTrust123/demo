package com.example.demo.service.Scheduled;

import com.example.demo.DTO.responseDTO.*;
import com.example.demo.db.entity.*;
import com.example.demo.db.repository.AirParticulatesRepository;
import com.example.demo.db.repository.AirQualityRepository;
import com.example.demo.db.repository.CircuitRepository;
import com.example.demo.db.repository.TemperatureAndHumidityRepository;
import com.example.demo.service.ServiceAirParticulates;
import com.example.demo.service.ServiceAirQuality;
import com.example.demo.service.ServiceCircuit;
import com.example.demo.service.*;

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
    private ServiceCircuit serviceCircuit;
    @Autowired
    private ServiceAirQuality serviceAirQuality;
    @Autowired
    private ServiceAirParticulates serviceAirParticulates;

    @Autowired
    private TemperatureAndHumidityRepository tempRepo; // 存檔的地方
    @Autowired
    private CircuitRepository circuitRepo;
    @Autowired
    private AirQualityRepository aqRepo;
    @Autowired
    private AirParticulatesRepository apRepo;

    @Autowired
    private ServiceLog serviceLog;

    @Scheduled(fixedRate = 300000)
    public void archiveTempData() {

        Collection<ResponseTemperatureAndHumidityDTO> currentData = serviceTemparatureAndHumidity
                .getAllTemparatureAndHumidityData();

        if (currentData == null || currentData.isEmpty()) {
            log.warn("no temperature and humidity data to archive, skip this round");
            serviceLog.record("WARN", "ServiceHistoryArchive",
                    "no temperature and humidity data to archive, skip this round");
            return;
        }

        List<TemperatureAndHumidity> entitiesToSave = new ArrayList<>();

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

    @Scheduled(fixedRate = 100000)
    public void archiveCircuitData() {
        Collection<ResponseCircuitDTO> currentData = serviceCircuit.getAllCircuitData();
        if (currentData == null || currentData.isEmpty()) {
            log.warn("no circuit data to archive, skip this round");
            serviceLog.record("WARN", "ServiceHistoryArchive", "no circuit data to archive, skip this round");
            return;
        }
        List<Circuit> entitiesToSave = new ArrayList<>();
        for (ResponseCircuitDTO dto : currentData) {
            Circuit entity = new Circuit();
            entity.setDeviceId(dto.deviceId());
            entity.setVoltage(dto.voltage());
            entity.setCurrent(dto.current());
            entity.setPower(dto.power());
            entity.setEnergy(dto.energy());
            entity.setTimestamp(dto.timestamp());

            entitiesToSave.add(entity);
        }
        circuitRepo.saveAll(entitiesToSave);
        log.info("circuit seces put in db");
    }

    @Scheduled(fixedRate = 300000)
    public void archiveAirQualityData() {
        Collection<ResponseAirQualityDTO> currentData = serviceAirQuality.getAllAirQualityData();
        if (currentData == null || currentData.isEmpty()) {
            log.warn("no air quality data to archive, skip this round");
            serviceLog.record("WARN", "ServiceHistoryArchive", "no air quality data to archive, skip this round");
            return;
        }
        List<AirQuality> entitiesToSave = new ArrayList<>();
        for (ResponseAirQualityDTO dto : currentData) {
            AirQuality entity = new AirQuality();
            entity.setDeviceId(dto.deviceId());
            entity.setAirPollution(dto.pm2_5());
            entity.setTimestamp(dto.timestamp());

            entitiesToSave.add(entity);
        }
        aqRepo.saveAll(entitiesToSave);
        log.info("air quality seces put in db");
    }

    @Scheduled(fixedRate = 300000)
    public void archiveAirParticulatesData() {
        Collection<ResponseAirParticulatesDTO> currentData = serviceAirParticulates.getAllAirParticalData();
        if (currentData == null || currentData.isEmpty()) {
            log.warn("no air particulates data to archive, skip this round");
            serviceLog.record("WARN", "ServiceHistoryArchive", "no air particulates data to archive, skip this round");
            return;
        }
        List<AirParticulates> entitiesToSave = new ArrayList<>();
        for (ResponseAirParticulatesDTO dto : currentData) {
            AirParticulates entity = new AirParticulates();
            entity.setDeviceId(dto.deviceId());
            entity.setPm2_5(dto.pm2_5());
            entity.setTimestamp(dto.timestamp());

            entitiesToSave.add(entity);
        }
        apRepo.saveAll(entitiesToSave);
        log.info("air particalutes seces put in db");
    }

}