package com.example.demo.priorityQueueTask; // 記得改成妳的路徑

import org.springframework.stereotype.Service;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.CompletableFuture;

@Service
public class QueueService {

    private final PriorityBlockingQueue<JobTask<?>> queue = new PriorityBlockingQueue<>();

    public <T> CompletableFuture<Object> addRequestToQueue(int priority, T data, String taskType) {
        JobTask<T> task = new JobTask<>(priority, data, taskType);
        queue.put(task);
        return task.getFuture();
    }

    public int getQueueSize() {
        return queue.size();
    }

    public JobTask<?> takeTask() throws InterruptedException {
        return queue.take();
    }
}