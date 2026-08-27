package com.example.demo.priorityQueueTask;

import java.util.concurrent.CompletableFuture;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class JobTask<T> implements Comparable<JobTask<T>> {

    private final int priority;
    private final T data;

    private final CompletableFuture<Object> future;
    private final String taskType;
    private Authentication auth;

    public JobTask(int priority, T data, String taskType, Authentication auth) {
        this.priority = priority;
        this.data = data;
        this.taskType = taskType;
        this.auth = auth;
        this.future = new CompletableFuture<>();
    }

    public JobTask(int priority, T data, String taskType) {
        this(priority, data, taskType, SecurityContextHolder.getContext().getAuthentication());
    }

    @Override
    public int compareTo(JobTask<T> other) {
        return Integer.compare(other.priority, this.priority);
    }

    public String getTaskType() {
        return taskType;
    }

    public T getData() {
        return data;
    }

    public CompletableFuture<Object> getFuture() {
        return future;
    }

    public Authentication getAuth() {
        return auth;
    }

    public void setAuth(Authentication auth) {
        this.auth = auth;
    }

    @Override
    public String toString() {
        return "JobTask{priority=" + priority + ", data=" + data + "}";
    }
}