package com.example.demo.service;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.responseDTO.ResponseAllFilterLoadDTO;
import com.example.demo.DTO.responseDTO.ResponseAllLoadDTO;
import com.example.demo.DTO.responseDTO.ResponseLoadDTO;
import com.example.demo.priorityQueueTask.QueueService;

@Service
public class ServiceLoad {
    @Autowired
    private QueueService queue;
    @Autowired
    private ServiceDelayTime DT;

    public ResponseLoadDTO getThreadStats() {
        ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(false, false);

        int blocked = 0;
        int runnable = 0;
        int waiting = 0;
        int timed_waiting = 0;

        for (ThreadInfo t : threads) {
            switch (t.getThreadState()) {
                case RUNNABLE:
                    runnable++;
                    break;
                case WAITING:
                    waiting++;
                    break;
                case TIMED_WAITING:
                    timed_waiting++;
                    break;
                case BLOCKED:
                    blocked++;
                    break;
                default:
                    break;
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("timed_waiting", timed_waiting);
        result.put("waiting", waiting);
        result.put("blocked", blocked);
        result.put("runnable", runnable);
        return new ResponseLoadDTO(
                result);
    }

    public ResponseAllLoadDTO getAllLoad() {
        ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(false, false);

        int blocked = 0;
        int runnable = 0;
        int waiting = 0;
        int timed_waiting = 0;

        for (ThreadInfo t : threads) {
            switch (t.getThreadState()) {
                case RUNNABLE:
                    runnable++;
                    break;
                case WAITING:
                    waiting++;
                    break;
                case TIMED_WAITING:
                    timed_waiting++;
                    break;
                case BLOCKED:
                    blocked++;
                    break;
                default:
                    break;
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("timed_waiting", timed_waiting);
        result.put("waiting", waiting);
        result.put("blocked", blocked);
        result.put("runnable", runnable);
        return new ResponseAllLoadDTO(
                result, queue.getQueueSize());
    }

    public ResponseAllLoadDTO getThreadFilterStats() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threads = threadMXBean.dumpAllThreads(false, false);

        int totalChefs = 0;
        int busyChefs = 0;
        int idleChefs = 0;
        int blockedChefs = 0;

        for (ThreadInfo t : threads) {
            if (t == null || t.getThreadName() == null)
                continue;
            if (t.getThreadName().startsWith("Chef-Thread-")) {
                totalChefs++;

                switch (t.getThreadState()) {
                    case RUNNABLE:
                        busyChefs++;
                        break;
                    case WAITING:
                    case TIMED_WAITING:
                        idleChefs++;
                        break;
                    case BLOCKED:
                        blockedChefs++;
                        break;
                    default:
                        break;
                }
            }
        }

        double utilization = totalChefs > 0 ? ((double) busyChefs / totalChefs) * 100 : 0.0;

        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("total_chefs", totalChefs);
        result.put("busy_chefs", busyChefs);
        result.put("idle_chefs", idleChefs);
        result.put("blocked_chefs", blockedChefs);
        result.put("utilization", (int) Math.round(utilization));

        return new ResponseAllLoadDTO(result, queue.getQueueSize());
    }

    public ResponseAllFilterLoadDTO getAllFilterLoad() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threads = threadMXBean.dumpAllThreads(false, false);

        int totalChefs = 0;
        int busyChefs = 0;
        int idleChefs = 0;
        int blockedChefs = 0;

        for (ThreadInfo t : threads) {
            if (t == null || t.getThreadName() == null)
                continue;
            if (t.getThreadName().startsWith("Chef-Thread-")) {
                totalChefs++;

                switch (t.getThreadState()) {
                    case RUNNABLE:
                        busyChefs++;
                        break;
                    case WAITING:
                    case TIMED_WAITING:
                        idleChefs++;
                        break;
                    case BLOCKED:
                        blockedChefs++;
                        break;
                    default:
                        break;
                }
            }
        }

        double utilization = totalChefs > 0 ? ((double) busyChefs / totalChefs) * 100 : 0.0;

        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("total_chefs", totalChefs);
        result.put("busy_chefs", busyChefs);
        result.put("idle_chefs", idleChefs);
        result.put("blocked_chefs", blockedChefs);
        result.put("utilization", (int) Math.round(utilization));

        return new ResponseAllFilterLoadDTO(result, queue.getQueueSize(), DT.getLatestProcessTime());
    }
}
