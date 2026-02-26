package com.example.demo.priorityQueueTask;

import com.example.demo.service.history.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import com.example.demo.DTO.requestDTO.history.*;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.demo.service.*;
import org.slf4j.Logger;

import com.example.demo.DTO.requestDTO.*;
import com.example.demo.DTO.requestDTO.PLCRequestDTO.RequestDPointStateDTO;
import com.example.demo.DTO.requestDTO.PLCRequestDTO.RequestMPointStateDTO;
import com.example.demo.DTO.requestDTO.PLCRequestDTO.RequestWriteMPointDTO;
import com.example.demo.DTO.requestDTO.PLCRequestDTO.RequestWriteDPointDTO;

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
    @Autowired
    private ServiceDeviceState serviceDeviceState;
    @Autowired
    private ServiceLog serviceLog;

    @Autowired
    private ServiceHistoryTemparatureAndHumidity serviceHistoryTemparatureAndHumidity;
    @Autowired
    private ServiceHistoryCircuit serviceHistoryCircuit;
    @Autowired
    private ServiceHistoryAirQuality serviceHistoryAirQuality;
    @Autowired
    private ServiceHistoryAirParticulates serviceHistoryAirParticulates;

    private ExecutorService executor;

    TaskProcessor(ServiceHistoryTemparatureAndHumidity serviceHistoryTemparatureAndHumidity) {
        this.serviceHistoryTemparatureAndHumidity = serviceHistoryTemparatureAndHumidity;
    }

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
                        String type = task.getTaskType();
                        Object result = null;

                        // 分流開始
                        switch (type) {
                            case "receiveTemparatureAndHumidityData":
                                RequestTemperatureAndHumidityDTO data_temp = (RequestTemperatureAndHumidityDTO) task
                                        .getData();
                                serviceDeviceState.updateHeartbeat(data_temp.getDeviceId());
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
                                break;

                            case "MpointState":
                                RequestMPointStateDTO rqMPoint = (RequestMPointStateDTO) task.getData();
                                String mPointParam = rqMPoint.param();
                                result = servicePLC.MpointState(mPointParam);
                                break;

                            case "DPointData":
                                RequestDPointStateDTO rqDPoint = (RequestDPointStateDTO) task.getData();
                                String dPointParam = rqDPoint.param();
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
                                RequestWriteMPointDTO dataMapM = (RequestWriteMPointDTO) task.getData();
                                String paramM = (String) dataMapM.param();
                                Boolean valueM = (Boolean) dataMapM.value();
                                result = servicePLC.writeMPoint(paramM, valueM);
                                break;

                            case "writeDPoint":
                                RequestWriteDPointDTO dataMapD = (RequestWriteDPointDTO) task.getData();
                                String paramD = (String) dataMapD.param();
                                Integer valueD = (Integer) dataMapD.value();
                                result = servicePLC.writeDPoint(paramD, valueD);
                                break;
                            case "getCountMetal":
                                result = servicePLC.getCountMetal();
                                break;
                            case "getCountNonMetal":
                                result = servicePLC.getCountNonMetal();
                                break;
                            case "ALLData":
                                result = serviceData.AllData();
                                break;
                            case "AllDeviceState":
                                result = serviceDeviceState.getAllDeviceStatuses();
                                break;
                            case "AllDataAndDeviceState":
                                result = serviceData.AllDataAndDeviceState();
                                break;
                            case "receiveCircuitData":
                                RequestCircuitDTO data_cir = (RequestCircuitDTO) task.getData();
                                serviceDeviceState.updateHeartbeat(data_cir.getDeviceId());
                                result = serviceCircuit.receiveCircuitData(data_cir);
                                break;

                            case "getCircuitData":
                                String deviceId_cir = (String) task.getData();
                                result = serviceCircuit.getCircuitData(deviceId_cir);
                                break;

                            case "getAllCircuitData":
                                result = serviceCircuit.getAllCircuitData();
                                break;

                            case "receiveCamData":
                                RequestCamDTO data_cam = (RequestCamDTO) task.getData();
                                serviceDeviceState.updateHeartbeat(data_cam.getDeviceId());
                                result = serviceCam.receiveCamData(data_cam);
                                break;

                            case "getCamData":
                                String deviceId_cam = (String) task.getData();
                                result = serviceCam.getCamData(deviceId_cam);
                                break;

                            case "getAllCamData":
                                result = serviceCam.getAllCamData();
                                break;

                            case "recriveAirQuality":
                                RequestAirQualityDTO data_air_qua = (RequestAirQualityDTO) task.getData();
                                serviceDeviceState.updateHeartbeat(data_air_qua.getDeviceId());
                                result = serviceAirQuality.recriveAirQuality(data_air_qua);
                                break;

                            case "getAirQualityData":
                                String deviceId_air_qua = (String) task.getData();
                                result = serviceAirQuality.getAirQualityData(deviceId_air_qua);
                                break;

                            case "getAllAirQualityData":
                                result = serviceAirQuality.getAllAirQualityData();
                                break;

                            case "recriveAirPartical":
                                RequestAirParticulatesDTO data_air_par = (RequestAirParticulatesDTO) task.getData();
                                serviceDeviceState.updateHeartbeat(data_air_par.getDeviceId());
                                result = serviceAirParticulates.recriveAirPartical(data_air_par);
                                break;

                            case "getAirParticalData":
                                String deviceId_air_par = (String) task.getData();
                                result = serviceAirParticulates.getAirParticalData(deviceId_air_par);
                                break;

                            case "getAllAirParticalData":
                                result = serviceAirParticulates.getAllAirParticalData();
                                break;
                            case "getTemparatureAndHumidityHistory":
                                RequestHistoryTemparatureAndHumidityDTO data_TemperatureAndHumidity = (RequestHistoryTemparatureAndHumidityDTO) task
                                        .getData();
                                result = serviceHistoryTemparatureAndHumidity.getTempHistory(
                                        data_TemperatureAndHumidity.deviceId(), data_TemperatureAndHumidity.start(),
                                        data_TemperatureAndHumidity.end());
                                break;
                            case "getCircuitHistory":
                                RequestHistoryCircuitDTO data_Circuit = (RequestHistoryCircuitDTO) task.getData();
                                result = serviceHistoryCircuit.getCircuitHistory(
                                        data_Circuit.deviceId(), data_Circuit.start(),
                                        data_Circuit.end());
                                break;
                            case "getAirQualityHistory":
                                RequestHistoryAirQualityDTO data_AirQuality = (RequestHistoryAirQualityDTO) task
                                        .getData();
                                result = serviceHistoryAirQuality.getAirQualityHistory(data_AirQuality.deviceId(),
                                        data_AirQuality.start(), data_AirQuality.end());
                                break;
                            case "getAirParticulatesHistory":
                                RequestHistoryAirParticulatesDTO data_AirParticulates = (RequestHistoryAirParticulatesDTO) task
                                        .getData();
                                result = serviceHistoryAirParticulates.getAirParticulatesHistory(
                                        data_AirParticulates.deviceId(), data_AirParticulates.start(),
                                        data_AirParticulates.end());
                                break;
                            case "getAllLogs":
                                result = serviceLog.getAllLogs();
                                break;
                            case "getErrorLogs":
                                result = serviceLog.getErrorLogs();
                                break;
                            case "getWarnLogs":
                                result = serviceLog.getWarnLogs();
                                break;
                            case "getLogByTime":
                                RequestLogDTO data_Log = (RequestLogDTO) task.getData();
                                result = serviceLog.getLogsByTime(data_Log.start(), data_Log.end());
                                break;

                            default:
                                log.warn("Unknown task type: {}", type);
                                serviceLog.record("WARN", "TaskProcessor", "Unknown task type: " + type);
                                break;
                        }

                        if (task.getFuture() != null) {
                            task.getFuture().complete(result);
                            log.info("Task of type {} completed with result: {}", type, result);
                        }

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        SecurityContextHolder.clearContext();
                    }
                }
            });
        }
    }

    @PreDestroy
    public void stopWorking() {
        log.warn("system doing predestroy");
        serviceLog.record("WARN", "TaskProcessor", "system doing predestroy, start shutting down executor");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        log.warn("system shutdown");
        serviceLog.record("WARN", "TaskProcessor", "system shutdown, executor stopped");
    }
}