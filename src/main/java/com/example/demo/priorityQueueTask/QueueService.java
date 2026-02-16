package com.example.demo.priorityQueueTask; // 記得改成妳的路徑

import org.springframework.stereotype.Service;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.CompletableFuture;

@Service
public class QueueService {

    // 這就是那個「依優先權排序的籃子」
    // 使用 JobTask<?> 是因為我們不知道裡面裝的是哪種 JSON 資料，用問號代表「任何種類」
    private final PriorityBlockingQueue<JobTask<?>> queue = new PriorityBlockingQueue<>();

    /**
     * Controller 呼叫這個方法來「抽號碼牌」
     */
    public <T> CompletableFuture<Object> addRequestToQueue(int priority, T data) {
        // 1. 打包成 JobTask
        JobTask<T> task = new JobTask<>(priority, data);

        // 2. 丟進籃子裡
        queue.put(task);

        // 3. 把「回條」給 Controller，讓它在那邊等
        return task.getFuture();
    }

    // 暫時提供一個方法讓我們看看籃子裡有多少人 (測試用)
    public int getQueueSize() {
        return queue.size();
    }

    // 在 QueueService.java 加入這個
    public JobTask<?> takeTask() throws InterruptedException {
        // take() 會阻塞，如果籃子是空的，大廚就會在這裡打瞌睡，直到有單子進來
        return queue.take();
    }
}