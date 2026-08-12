package com.example.demo.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

@Service
public class ServiceDelayTime {
    private AtomicInteger lastestProcessTime = new AtomicInteger(0);

    public void logInLastestProcessTime(int time) {
        lastestProcessTime.set(time);
    }

    public int getLatestProcessTime() {
        return lastestProcessTime.get();
    }
}