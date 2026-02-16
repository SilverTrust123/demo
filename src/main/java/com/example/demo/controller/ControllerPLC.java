package com.example.demo.controller;

import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ServicePLC;
import org.slf4j.Logger;

@RestController
@RequestMapping("/plc")
public class ControllerPLC {

    private boolean plcConnected = false;
    private static final Logger log = LoggerFactory.getLogger(ControllerPLC.class);

    @Autowired
    private ServicePLC servicePLC;

    @GetMapping("/PLCConnect")
    public String PLCConnect() {
        plcConnected = servicePLC.isPlcConnected();
        log.info("PLC Connected: {}", plcConnected);
        return "PLC Connected: " + plcConnected;
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
}
