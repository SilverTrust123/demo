package com.example.demo.priorityQueueTask;

import java.util.concurrent.CompletableFuture;

public class JobTask<T> implements Comparable<JobTask<T>> {

    private final int priority; // 優先權：數字越大越重要
    private final T data; // 這就是你原本 POST/GET 拿到的資料

    // 這是用來讓 Controller 等結果的「回條」
    private final CompletableFuture<Object> future;
    private final String taskType;

    public JobTask(int priority, T data, String taskType) {
        this.priority = priority;
        this.data = data;
        this.taskType = taskType;
        this.future = new CompletableFuture<>();
    }

    // 這是給 PriorityQueue 看的，決定誰排前面
    // 我們設定：priority 數字大的排前面 (例如 100 > 1)
    @Override
    public int compareTo(JobTask<T> other) {
        return Integer.compare(other.priority, this.priority);
    }

    public String getTaskType() {
        return taskType;
    }

    // 取出資料的方法
    public T getData() {
        return data;
    }

    // 取出回條的方法
    public CompletableFuture<Object> getFuture() {
        return future;
    }

    // 為了等等測試方便，我們加個 toString
    @Override
    public String toString() {
        return "JobTask{priority=" + priority + ", data=" + data + "}";
    }
}