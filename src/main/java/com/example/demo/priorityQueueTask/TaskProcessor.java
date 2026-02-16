package com.example.demo.priorityQueueTask;

import com.example.demo.priorityQueueTask.JobTask;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.demo.sensor.SensorDataTemperatureAndHumidity;
import com.example.demo.service.*;

@Component
public class TaskProcessor {

    @Autowired
    private QueueService queueService;

    @Autowired
    private ServiceTemparatureAndHumidity yourService; // 這裡要換成妳原本那個 @Autowired 的主邏輯 Service 名稱

    @PostConstruct // 這代表 Spring 一啟動，這個方法就會自動執行
    public void startWorking() {
        // 啟動一個新線程，不然會把主程式卡死
        new Thread(() -> {
            while (true) { // 無限迴圈，大廚永遠在廚房待命
                try {
                    // 1. 從櫃檯拿單子 (會依照優先權，重要的先拿)
                    JobTask<?> task = queueService.takeTask();

                    // 2. 執行妳原本的邏輯 (這就是妳原本拆分好的 @Service)
                    // 假設 task.getData() 拿出來的是 SensorData...
                    Object result = yourService
                            .receiveTemparatureAndHumidityData((SensorDataTemperatureAndHumidity) task.getData());

                    // 3. 關鍵一步：把結果填進「回條」裡！
                    // 這時候前端轉圈圈的 Postman 就會拿到結果了
                    task.getFuture().complete(result);

                } catch (Exception e) {
                    e.printStackTrace(); // 如果處理出錯，印出來看看
                }
            }
        }).start();
    }
}