package com.example.demo.priorityQueueTask;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.CompletableFuture;

@Service
public class QueueService {

    private final PriorityBlockingQueue<JobTask<?>> queue = new PriorityBlockingQueue<>();

    public <T> CompletableFuture<Object> addRequestToQueue(int priority, T data, String taskType, Authentication auth) {
        JobTask<T> task = new JobTask<>(priority, data, taskType, auth);
        queue.put(task);
        return task.getFuture();
    }

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