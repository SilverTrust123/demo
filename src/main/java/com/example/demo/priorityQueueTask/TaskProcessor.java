package com.example.demo.priorityQueueTask;

import jakarta.annotation.PostConstruct;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.demo.sensor.SensorDataTemperatureAndHumidity;
import com.example.demo.service.*;

@Component
public class TaskProcessor {

    @Autowired
    private QueueService queueService;

    @Autowired
    private ServiceTemparatureAndHumidity serviceTemparatureAndHumidity;
    @Autowired
    private ServicePLC servicePLC;
    @Autowired
    private ServiceData serviceData;

    @PostConstruct // 這代表 Spring 一啟動，這個方法就會自動執行
    public void startWorking() {
        // 啟動一個新線程，不然會把主程式卡死
        new Thread(() -> {

            while (true) {
                try {
                    JobTask<?> task = queueService.takeTask();
                    String type = task.getTaskType(); // 看看標籤寫什麼
                    Object result = null;

                    // --- 分流開始 ---
                    switch (type) {
                        case "receiveTemparatureAndHumidityData":
                            SensorDataTemperatureAndHumidity data1 = (SensorDataTemperatureAndHumidity) task.getData();
                            result = serviceTemparatureAndHumidity.receiveTemparatureAndHumidityData(data1);
                            break;
                        case "getTemparatureAndHumidityData":
                            String deviceId = (String) task.getData();
                            result = serviceTemparatureAndHumidity.getTemparatureAndHumidityData(deviceId);
                            break;
                        case "getAllTemparatureAndHumidityData":
                            result = serviceTemparatureAndHumidity.getAllTemparatureAndHumidityData();
                            break;
                        case "PLCConnect":
                            result = servicePLC.isPlcConnected();
                        case "MpointState":
                            String mPointParam = (String) task.getData();
                            result = servicePLC.MpointState(mPointParam);
                            break;
                        case "DPointData":
                            String dPointParam = (String) task.getData();
                            result = servicePLC.DPointData(dPointParam);
                            break;
                        case "AllDPointData":
                            result = servicePLC.AllDPointData();
                            break;
                        case "AllMPointData":
                            result = servicePLC.AllMPointData();
                            break;
                        case "plcState":
                            result = servicePLC.plcState();
                            break;
                        case "writeMPoint":
                            @SuppressWarnings("unchecked")
                            Map<String, Object> dataMapM = (Map<String, Object>) task.getData();
                            result = servicePLC.writeMPoint(dataMapM);
                            break;
                        case "writeDPoint":
                            @SuppressWarnings("unchecked")
                            Map<String, Object> dataMapD = (Map<String, Object>) task.getData();
                            result = servicePLC.writeDPoint(dataMapD);
                            break;
                        case "ALLData":
                            result = serviceData.AllData();
                        default:
                            System.out.println("收到未知的任務類型：" + type);
                            break;
                    }
                    // --- 分流結束 ---

                    // 把結果回傳
                    if (task.getFuture() != null) {
                        task.getFuture().complete(result);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}