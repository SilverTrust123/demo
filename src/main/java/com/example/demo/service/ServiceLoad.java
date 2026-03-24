package com.example.demo.service;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.DTO.responseDTO.ResponseLoadDTO;

@Service
public class ServiceLoad {
    public ResponseLoadDTO getThreadStats() {
        ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(false, false);

        int working = 0;
        int sleeping = 0;
        int blocked = 0;

        for (ThreadInfo t : threads) {
            switch (t.getThreadState()) {
                case RUNNABLE:
                    working++;
                    break;
                case WAITING:
                case TIMED_WAITING:
                    sleeping++;
                    break;
                case BLOCKED:
                    blocked++;
                    break;
                default:
                    break;
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("working", working);
        result.put("sleeping", sleeping);
        result.put("blocked", blocked);
        return new ResponseLoadDTO(
                result);
    }
}
