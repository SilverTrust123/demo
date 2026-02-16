package com.example.demo;

import com.example.demo.cam.SensorDataCam;
import com.example.demo.sensor.SensorDataAirParticulates;
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
        plcConnected = servicePLC.isPlcConnected();
        log.info("PLC Connected: {}", plcConnected);
        return "PLC Connected: " + plcConnected;
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
