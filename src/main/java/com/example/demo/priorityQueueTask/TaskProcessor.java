package com.example.demo.priorityQueueTask;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.demo.cam.SensorDataCam;
import com.example.demo.sensor.SensorDataAirParticulates;
import com.example.demo.sensor.SensorDataAirQuality;
import com.example.demo.sensor.SensorDataCircuit;
import com.example.demo.sensor.SensorDataTemperatureAndHumidity;
import com.example.demo.service.*;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class TaskProcessor {
    private static final Logger log = LoggerFactory.getLogger(ServiceCircuit.class);

    @Value("${thread_count}")
    private int threadCount;

    @Autowired
    private QueueService queueService;

    @Autowired
    private ServiceTemparatureAndHumidity serviceTemparatureAndHumidity;
    @Autowired
    private ServicePLC servicePLC;
    @Autowired
    private ServiceData serviceData;
    @Autowired
    private ServiceCircuit serviceCircuit;
    @Autowired
    private ServiceCam serviceCam;
    @Autowired
    private ServiceAirQuality serviceAirQuality;
    @Autowired
    private ServiceAirParticulates serviceAirParticulates;

    private ExecutorService executor;

    @PostConstruct
    public void startWorking() {
        executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int chefId = i + 1;

            // 啟動一個新線程，不然會把主程式卡死
            executor.submit(() -> {
                Thread.currentThread().setName("Chef-Thread-" + chefId);

                while (true) {
                    try {
                        JobTask<?> task = queueService.takeTask();
                        String type = task.getTaskType(); // 看看標籤寫什麼
                        Object result = null;

                        // --- 分流開始 ---
                        switch (type) {
                            case "receiveTemparatureAndHumidityData":
                                SensorDataTemperatureAndHumidity data_temp = (SensorDataTemperatureAndHumidity) task
                                        .getData();
                                result = serviceTemparatureAndHumidity
                                        .receiveTemparatureAndHumidityData(data_temp);
                                break;

                            case "getTemparatureAndHumidityData":
                                String deviceId_temp = (String) task.getData();
                                result = serviceTemparatureAndHumidity
                                        .getTemparatureAndHumidityData(deviceId_temp);
                                break;

                            case "getAllTemparatureAndHumidityData":
                                result = serviceTemparatureAndHumidity
                                        .getAllTemparatureAndHumidityData();
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

                            case "receiveCircuitData":
                                SensorDataCircuit data_cir = (SensorDataCircuit) task.getData();
                                result = serviceCircuit.receiveCircuitData(data_cir);

                            case "getCircuitData":
                                String deviceId_cir = (String) task.getData();
                                result = serviceCircuit.getCircuitData(deviceId_cir);
                                break;

                            case "getAllCircuitData":
                                result = serviceCircuit.getAllCircuitData();

                            case "receiveCamData":
                                SensorDataCam data_cam = (SensorDataCam) task.getData();
                                result = serviceCam.receiveCamData(data_cam);

                            case "getCamData":
                                String deviceId_cam = (String) task.getData();
                                result = serviceCam.getCamData(deviceId_cam);

                            case "getAllCamData":
                                result = serviceCam.getAllCamData();

                            case "recriveAirQuality":
                                SensorDataAirQuality data_air_qua = (SensorDataAirQuality) task.getData();
                                result = serviceAirQuality.recriveAirQuality(data_air_qua);

                            case "getAirQualityData":
                                String deviceId_air_qua = (String) task.getData();
                                result = serviceAirQuality.getAirQualityData(deviceId_air_qua);

                            case "getAllAirQualityData":
                                result = serviceAirQuality.getAllAirQualityData();

                            case "recriveAirPartical":
                                SensorDataAirParticulates data_air_par = (SensorDataAirParticulates) task.getData();
                                result = serviceAirParticulates.recriveAirPartical(data_air_par);

                            case "getAirParticalData":
                                String deviceId_air_par = (String) task.getData();
                                result = serviceAirParticulates.getAirParticalData(deviceId_air_par);

                            case "getAllAirParticalData":
                                result = serviceAirParticulates.getAllAirParticalData();

                            default:
                                log.warn("Unknown task type: {}", type);
                                break;
                        }
                        // --- 分流結束 ---

                        // 把結果回傳
                        if (task.getFuture() != null) {
                            task.getFuture().complete(result);
                            log.info("Task of type {} completed with result: {}", type, result);
                        }

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @PreDestroy
    public void stopWorking() {
        log.warn("system doing predestroy");

        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        log.warn("system shutdown");
    }
}