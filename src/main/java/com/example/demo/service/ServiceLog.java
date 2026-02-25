package com.example.demo.service;

import com.example.demo.db.entity.Log;
import com.example.demo.db.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceLog {

    @Autowired
    private SystemLogRepository logRepo;

    // 取得 SLF4J 的 Logger
    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    // 專門存記錄的通用方法
    public void record(String level, String source, String message, String deviceId) {
        // 1. 同時印在控制台 (方便開發時看)
        switch (level.toUpperCase()) {
            case "INFO" -> logger.info("[{}] {}: {}", source, deviceId, message);
            case "ERROR" -> logger.error("[{}] {}: {}", source, deviceId, message);
            case "WARN" -> logger.warn("[{}] {}: {}", source, deviceId, message);
        }

        // 2. 存入資料庫 (為了以後查閱)
        SystemLog log = new SystemLog();
        log.setLogLevel(level);
        log.setSource(source);
        log.setMessage(message);
        log.setDeviceId(deviceId);
        log.setTimestamp(System.currentTimeMillis());

        logRepo.save(log);
    }
}