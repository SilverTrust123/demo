package com.example.demo.service.Scheduled;

import java.util.Random;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.cdimascio.dotenv.Dotenv;
import com.example.demo.service.ServicePLC;
import org.slf4j.Logger;

@Component
public class ServiceGenerateWrong {
    Dotenv dotenv = Dotenv.load();
    private final boolean generate_wrong = Boolean.parseBoolean(dotenv.get("generate_wrong"));

    @Autowired
    private ServicePLC plc;
    private static final Logger log = LoggerFactory.getLogger(ServiceGenerateWrong.class);

    private final Random rand = new Random();

    @Scheduled(fixedRate = 10000)
    public void generateWrong() {
        if (generate_wrong) {
            if (rand.nextBoolean()) {
                plc.generate_wrong();
                log.info("success trigger generate wrong");
            }
        }
    }
}
