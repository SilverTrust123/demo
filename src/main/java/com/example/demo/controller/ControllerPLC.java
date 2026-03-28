package com.example.demo.controller;

import java.util.concurrent.CompletableFuture;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.priorityQueueTask.QueueService;
import com.example.demo.DTO.requestDTO.PLCRequestDTO.*;

import org.slf4j.Logger;

@RestController
@RequestMapping("/plc")
public class ControllerPLC {

    private boolean plcConnected = false;
    private static final Logger log = LoggerFactory.getLogger(ControllerPLC.class);
    @Value("${important}")
    private int IMPORTANT;
    @Value("${normal}")
    private int NORMAL;
    @Value("${urgent}")
    private int URGENT;
    @Autowired
    private QueueService queueService;

    @GetMapping("/PLCConnect")
    public CompletableFuture<Object> PLCConnect() {
        log.info("PLC Connected: {}", plcConnected);
        return queueService.addRequestToQueue(NORMAL, null, "PLCConnect");
    }

    // -----------
    // 數位雙生：讀取 M 點狀態
    // 回傳：NoDevice / Error / 真偽值
    // @RequestParam(required = false) String param
    @GetMapping("/MPointState")
    public String MpointState(@RequestBody RequestMPointStateDTO param) {
        log.info("transfer received read m point request {} ", param);
        queueService.addRequestToQueue(IMPORTANT, param, "MpointState");
        return new String("ok");
    }

    // -----------
    // 詢問現在參數：讀取 D 點數值
    // 回傳：NoDevice / Error / 實際數值
    @GetMapping("/DPointData")
    public String DPointData(@RequestBody RequestDPointStateDTO param) {
        log.info("transfer received read d point request {} ", param);
        queueService.addRequestToQueue(IMPORTANT, param, "DPointData");
        return new String("ok");
    }

    @GetMapping("/AllDPointData")
    public String AllDPointData() throws Exception {
        log.info("transfer received read all DPoint request");
        queueService.addRequestToQueue(IMPORTANT, null, "AllDPointData");
        return new String("ok");
    }

    @GetMapping("/AllMPointData")
    public String AllMPointData() throws Exception {
        log.info("transfer received read all MPoint request");
        queueService.addRequestToQueue(IMPORTANT, null, "AllMPointData");
        return new String("ok");
    }

    @GetMapping("/state")
    public String plcState() {
        log.info("transfer received plc state request");
        queueService.addRequestToQueue(IMPORTANT, null, "plcState");
        return new String("ok");
    }

    // ----------------
    @PostMapping("/writeMPoint")
    public String writeMPoint(@RequestBody RequestWriteMPointDTO payload) {
        String param = (String) payload.param();
        Boolean value = (Boolean) payload.value();
        log.info("transfer received write MPoint request with param {} and value {}", param, value);
        queueService.addRequestToQueue(URGENT, payload, "writeMPoint");
        return new String("ok");
    }

    // ---------------------------------
    @PostMapping("/writeDPoint")
    public String writeDPoint(@RequestBody RequestWriteDPointDTO payload) {
        String param = (String) payload.param();
        Integer value = (Integer) payload.value();
        log.info("transfer received write DPoint request with param {} and value {}", param, value);
        queueService.addRequestToQueue(URGENT, payload, "writeDPoint");
        return new String("ok");
    }

    @GetMapping("getCountMetal")
    public String getCountMetal() {
        log.info("transfer received get count metal request");
        queueService.addRequestToQueue(IMPORTANT, null, "getCountMetal");
        return new String("ok");
    }

    @GetMapping("getCountNonMetal")
    public String getCountNonMetal() {
        log.info("transfer received get count non metal request");
        queueService.addRequestToQueue(IMPORTANT, null, "getCountNonMetal");
        return new String("ok");
    }

    @GetMapping("test")
    public String test() {
        log.info("test");
        queueService.addRequestToQueue(IMPORTANT, null, "test");
        return new String("ok");
    }

}
