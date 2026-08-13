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

        int totalthread = 0;
        int busythread = 0;
        int idlethread = 0;
        int blockedthread = 0;

        for (ThreadInfo t : threads) {
            if (t == null || t.getThreadName() == null)
                continue;
            if (t.getThreadName().startsWith("This-is-Thread-")) {
                totalthread++;

                switch (t.getThreadState()) {
                    case RUNNABLE:
                        busythread++;
                        break;
                    case WAITING:
                    case TIMED_WAITING:
                        idlethread++;
                        break;
                    case BLOCKED:
                        blockedthread++;
                        break;
                    default:
                        break;
                }
            }
        }

        double utilization = totalthread > 0 ? ((double) busythread / totalthread) * 100 : 0.0;

        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("total_thread", totalthread);
        result.put("busy_thread", busythread);
        result.put("idle_thread", idlethread);
        result.put("blocked_thread", blockedthread);
        result.put("utilization", (int) Math.round(utilization));

        return new ResponseAllLoadDTO(result, queue.getQueueSize());
    }

    public ResponseAllFilterLoadDTO getAllFilterLoad() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threads = threadMXBean.dumpAllThreads(false, false);

        int totalthread = 0;
        int busythread = 0;
        int idlethread = 0;
        int blockedthread = 0;

        for (ThreadInfo t : threads) {
            if (t == null || t.getThreadName() == null)
                continue;
            if (t.getThreadName().startsWith("This-is-Thread-")) {
                totalthread++;

                switch (t.getThreadState()) {
                    case RUNNABLE:
                        busythread++;
                        break;
                    case WAITING:
                    case TIMED_WAITING:
                        idlethread++;
                        break;
                    case BLOCKED:
                        blockedthread++;
                        break;
                    default:
                        break;
                }
            }
        }

        double utilization = totalthread > 0 ? ((double) busythread / totalthread) * 100 : 0.0;

        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("total_thread", totalthread);
        result.put("busy_thread", busythread);
        result.put("idle_thread", idlethread);
        result.put("blocked_thread", blockedthread);
        result.put("utilization", (int) Math.round(utilization));

        return new ResponseAllFilterLoadDTO(result, queue.getQueueSize(), DT.getLatestProcessTime());
    }
}
