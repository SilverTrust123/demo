package com.example.demo.service;

import com.example.demo.DTO.responseDTO.ResponseLogDTO;
import com.example.demo.db.entity.Log;
import com.example.demo.db.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceLog {
    @Autowired
    private LogRepository logRepo;

    private static final Logger log = LoggerFactory.getLogger(ServiceLog.class);

    public void record(String level, String source, String message) {
        log.info("save log");
        Log log = new Log();
        log.setLogLevel(level);
        log.setSource(source);
        log.setMessage(message);
        log.setTimestamp((int) (System.currentTimeMillis() / 1000L));

        logRepo.save(log);
    }

    public List<ResponseLogDTO> getAllLogs() {
        return logRepo.findAllByOrderByTimestampDesc()
                .stream()
                .map(entity -> new ResponseLogDTO(
                        entity.getId(),
                        entity.getLogLevel(),
                        entity.getSource(),
                        entity.getMessage(),
                        entity.getTimestamp()))
                .collect(Collectors.toList());
    }

    public List<ResponseLogDTO> getErrorLogs() {
        return logRepo.findByLogLevelOrderByTimestampDesc("ERROR").stream()
                .map(entity -> new ResponseLogDTO(entity.getId(),
                        entity.getLogLevel(),
                        entity.getSource(),
                        entity.getMessage(),
                        entity.getTimestamp()))
                .collect(Collectors.toList());
    }

    public List<ResponseLogDTO> getWarnLogs() {
        return logRepo.findByLogLevelOrderByTimestampDesc("WARN").stream()
                .map(entity -> new ResponseLogDTO(entity.getId(),
                        entity.getLogLevel(),
                        entity.getSource(),
                        entity.getMessage(),
                        entity.getTimestamp()))
                .collect(Collectors.toList());
    }

    public List<ResponseLogDTO> getLogsByTime(int start, int end) {
        return logRepo.findByTimestampBetweenOrderByTimestampDesc(start, end).stream()
                .map(entity -> new ResponseLogDTO(entity.getId(),
                        entity.getLogLevel(),
                        entity.getSource(),
                        entity.getMessage(),
                        entity.getTimestamp()))
                .collect(Collectors.toList());
    }
}