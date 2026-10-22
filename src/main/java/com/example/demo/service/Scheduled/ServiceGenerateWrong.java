package com.example.demo.service.Scheduled;

import java.time.Duration;
import java.util.Random;

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
public class ServiceGenerateWrong implements SchedulingConfigurer {

    private final Dotenv dotenv = Dotenv.load();
    private final boolean generate_wrong = Boolean.parseBoolean(dotenv.get("generate_wrong"));

    @Autowired
    private ServicePLC plc;

    private static final Logger log = LoggerFactory.getLogger(ServiceGenerateWrong.class);

    private int interval;
    private final Random rand = new Random();

    @PostConstruct
    private void fillInInterval() {
        interval = Integer.parseInt(
                dotenv.get("generate_wrong_interval"));
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addFixedRateTask(
                new IntervalTask(
                        this::generateWrong,
                        Duration.ofMillis(interval),
                        Duration.ZERO));
    }

    public void generateWrong() {
        if (generate_wrong && plc.isPlcConnected()) {
            if (rand.nextBoolean()) {
                plc.generate_wrong();
                log.info("success trigger generate wrong");
            }
        }
    }
}