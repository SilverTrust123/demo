package com.example.demo;

import com.example.demo.cam.SensorDataCam;
import com.example.demo.sensor.SensorDataAirParticulates;
import com.example.demo.sensor.SensorDataAirQuality;
import com.example.demo.sensor.SensorDataCircuit;
import com.example.demo.sensor.SensorDataTemperatureAndHumidity;
import com.example.demo.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "*")
@RestController
public class Controller {
    private boolean plcConnected = false;
    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private ServiceTemparatureAndHumidity serviceTemparatureAndHumidity;
    @Autowired
    private ServiceCircuit serviceCircuit;
    @Autowired
    private ServiceAirQuality serviceAirQuality;
    @Autowired
    private ServiceAirParticulates serviceAirParticulates;
    @Autowired
    private ServiceCam serviceCam;
    @Autowired
    private ServicePLC servicePLC;
    @Autowired
    private ServiceData serviceData;

    @GetMapping("/")
    public String home() {
        log.info("receive frontend check the backend");
        return "backend running";
    }

    @GetMapping("/PLCConnect")
    public String PLCConnect() {
        log.info("PLC Connected: {}", plcConnected);
        return "PLC Connected: " + plcConnected;
    }

    @PostMapping("/TemparatureAndHumidityData")
    public String receiveTemparatureAndHumidityData(@RequestBody SensorDataTemperatureAndHumidity data) {
        log.info("Received and transfertemperature and humidity data");
        return serviceTemparatureAndHumidity.receiveTemparatureAndHumidityData(data);
    }

    // 船空的回去就是找不到東西
    @GetMapping("/TemparatureAndHumidityData/{deviceId}")
    public SensorDataTemperatureAndHumidity getTemparatureAndHumidityData(@PathVariable String deviceId) {
        log.info("Received and transfer request for temperature and humidity data of device");
        return serviceTemparatureAndHumidity.getTemparatureAndHumidityData(deviceId);
    }

    // 打掉太舊的資料 然後回傳剩下的 可能會只剩一個 等一下要跟董事長說一下
    @GetMapping("/TemparatureAndHumidityData")
    public Collection<SensorDataTemperatureAndHumidity> getAllTemparatureAndHumidityData() {
        log.info("Received and transfer request for all temperature and humidity data");
        return serviceTemparatureAndHumidity.getAllTemparatureAndHumidityData();
    }

    @PostMapping("/CircuitData")
    public String receiveCircuitData(@RequestBody SensorDataCircuit data) {
        log.info("Received and transfer circuit data");
        return serviceCircuit.receiveCircuitData(data);
    }

    // 船空的回去就是找不到東西 或是太舊了
    @GetMapping("/CircuitData/{deviceId}")
    public SensorDataCircuit getCircuitData(@PathVariable String deviceId) {
        log.info("Received and transfer request for circuit data of device");
        return serviceCircuit.getCircuitData(deviceId);
    }

    // 這個本來最多就一個 如果船空的回去就是太舊了
    @GetMapping("/CircuitData")
    public Collection<SensorDataCircuit> getAllCircuitData() {
        log.info("Received and transfer request for all circuit data");
        return serviceCircuit.getAllCircuitData();
    }

    @PostMapping("/AirQualityData")
    public String recriveAirQuality(@RequestBody SensorDataAirQuality data) {
        log.info("Received and transfer air quality data");
        return serviceAirQuality.recriveAirQuality(data);
    }

    @GetMapping("/AirQualityData/{deviceId}")
    public SensorDataAirQuality getAirQualityData(@PathVariable String deviceId) {
        log.info("Received and transfer request for air quality data of device");
        return serviceAirQuality.getAirQualityData(deviceId);
    }

    @GetMapping("/AirQualityData")
    public Collection<SensorDataAirQuality> getAllAirQualityData() {
        log.info("Received and transfer request for all air quality data");
        return serviceAirQuality.getAllAirQualityData();
    }

    @PostMapping("/AirParticalData")
    public String recriveAirPartical(@RequestBody SensorDataAirParticulates data) {
        log.info("Received and transfer air particulates data");
        return serviceAirParticulates.recriveAirPartical(data);
    }

    @GetMapping("/AirParticalData/{deviceId}")
    public SensorDataAirParticulates getAirParticalData(@PathVariable String deviceId) {
        log.info("Received and transfer request for air particulates data of device");
        return serviceAirParticulates.getAirParticalData(deviceId);
    }

    @GetMapping("/AirParticalData")
    public Collection<SensorDataAirParticulates> getAllAirParticalData() {
        log.info("Received and transfer request for all air particulates data");
        return serviceAirParticulates.getAllAirParticalData();
    }

    @PostMapping("/CamData")
    public String receiveCamData(@RequestBody SensorDataCam data) {
        log.info("Received and transfer cam data");
        return serviceCam.receiveCamData(data);
    }

    @GetMapping("/CamData/{deviceId}")
    public SensorDataCam getCamData(@PathVariable String deviceId) {
        log.info("Received and transfer request for cam data of device");
        return serviceCam.getCamData(deviceId);
    }

    @GetMapping("/CamData")
    public Collection<SensorDataCam> getAllCamData() {
        log.info("Received and transfer request for all cam data");
        return serviceCam.getAllCamData();
    }

    // 數位雙生：讀取 M 點狀態
    // 回傳：NoDevice / Error / 真偽值
    @GetMapping("/plc/MPointState")
    public String MpointState(@RequestParam(required = false) String param) {
        log.info("transfer received read m point request {} ", param);
        return servicePLC.MpointState(param);
    }

    // 詢問現在參數：讀取 D 點數值
    // 回傳：NoDevice / Error / 實際數值
    @GetMapping("/plc/DPointData")
    public String DPointData(@RequestParam(required = false) String param) {
        log.info("transfer received read d point request {} ", param);
        return servicePLC.DPointData(param);
    }

    @GetMapping("/plc/AllDPointData")
    public Map<String, Integer> AllDPointData() throws Exception {
        log.info("transfer received read all DPoint request");
        return servicePLC.AllDPointData();
    }

    @GetMapping("/plc/AllMPointData")
    public Map<String, Boolean> AllMPointData() throws Exception {
        log.info("transfer received read all MPoint request");
        return servicePLC.AllMPointData();
    }

    @GetMapping("/plc/state")
    public String plcState() {
        log.info("transfer received plc state request");
        return servicePLC.plcState();
    }

    @PostMapping("/plc/writeMPoint")
    public String writeMPoint(@RequestBody Map<String, Object> payload) {
        String param = (String) payload.get("param");
        Boolean value = (Boolean) payload.get("value");
        log.info("transfer received write MPoint request with param {} and value {}", param, value);
        return servicePLC.writeMPoint(payload);
    }

    @PostMapping("/plc/writeDPoint")
    public String writeDPoint(@RequestBody Map<String, Object> payload) {
        String param = (String) payload.get("param");
        Object valueObj = payload.get("value");
        log.info("transfer received write DPoint request with param {} and value {}", param, valueObj);
        return servicePLC.writeDPoint(payload);
    }

    @GetMapping("/AllData")
    public ConcurrentHashMap<String, Object> AllData(@RequestParam String param) throws Exception {
        log.info("Received and transfer request for all data with param {}", param);
        return serviceData.AllData(param);
    }
}
