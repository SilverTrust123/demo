package com.example.demo.service.Scheduled;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

import com.example.demo.service.ServicePLC;

@Service
public class ServiceCheckOrReconnectPLC implements SchedulingConfigurer {

    private final Dotenv dotenv = Dotenv.load();

    @Autowired
    private ServicePLC plc;

    private static final Logger log = LoggerFactory.getLogger(ServiceCheckOrReconnectPLC.class);

    private int interval;

    @PostConstruct
    private void fillInInterval() {
        interval = Integer.parseInt(
                dotenv.get("check_interval"));
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addFixedRateTask(
                new IntervalTask(
                        this::checkOrReconnectPLC,
                        Duration.ofMillis(interval),
                        Duration.ZERO));
    }

    private void checkOrReconnectPLC() {
        log.warn("trigger reconnect ot plc ");
        if (plc.isPlcConnected()) {
            return;
        } else {
            plc.reconnectPLC();
        }
    }
}