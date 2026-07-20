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
        long curr = System.currentTimeMillis();
        log.info("PLC Connected: {}", plcConnected);
        return queueService.addRequestToQueue(NORMAL, null, "PLCConnect").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("Request PLCConnect been hjandle in " + (done - curr));
        });
    }

    // -----------
    // 數位雙生：讀取 M 點狀態
    // 回傳：NoDevice / Error / 真偽值
    // @RequestParam(required = false) String param
    @GetMapping("/MPointState")
    public CompletableFuture<Object> MpointState(@RequestBody RequestMPointStateDTO param) {
        long curr = System.currentTimeMillis();
        log.info("transfer received read m point request {} ", param);
        return queueService.addRequestToQueue(IMPORTANT, param, "MpointState").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("request MpointState been danle in " + (done - curr));
        });
    }

    // -----------
    // 詢問現在參數：讀取 D 點數值
    // 回傳：NoDevice / Error / 實際數值
    @GetMapping("/DPointData")
    public CompletableFuture<Object> DPointData(@RequestBody RequestDPointStateDTO param) {
        long curr = System.currentTimeMillis();
        log.info("transfer received read d point request {} ", param);
        return queueService.addRequestToQueue(IMPORTANT, param, "DPointData").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("request Dpoint Data been complete in " + (done - curr));
        });
    }

    @GetMapping("/AllDPointData")
    public CompletableFuture<Object> AllDPointData() throws Exception {
        long curr = System.currentTimeMillis();
        log.info("transfer received read all DPoint request");
        return queueService.addRequestToQueue(IMPORTANT, null, "AllDPointData").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("request ALLDpoint complate in " + (done - curr));
        });
    }

    @GetMapping("/AllMPointData")
    public CompletableFuture<Object> AllMPointData() throws Exception {
        long curr = System.currentTimeMillis();
        log.info("transfer received read all MPoint request");
        return queueService.addRequestToQueue(IMPORTANT, null, "AllMPointData").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("Request ALL m point completate in " + (done - curr));
        });
    }

    @GetMapping("/state")
    public CompletableFuture<Object> plcState() {
        long curr = System.currentTimeMillis();
        log.info("transfer received plc state request");
        return queueService.addRequestToQueue(IMPORTANT, null, "plcState").whenComplete((res, exp) -> {
            log.info("request plc state compleate in " + (System.currentTimeMillis() - curr));
        });
    }

    // ----------------
    @PostMapping("/writeMPoint")
    public String writeMPoint(@RequestBody RequestWriteMPointDTO payload) {
        long curr = System.currentTimeMillis();
        String param = (String) payload.param();
        Boolean value = (Boolean) payload.value();
        log.info("transfer received write MPoint request with param {} and value {}", param, value);
        queueService.addRequestToQueue(URGENT, payload, "writeMPoint").whenComplete((res, exp) -> {
            log.info("request compleate write m point in " + (System.currentTimeMillis() - curr));
        });
        return new String("ok");
    }
    // @PostMapping("/writeMPoint")
    // public CompletableFuture<Object> writeMPoint(@RequestBody
    // RequestWriteMPointDTO payload) {
    // String param = (String) payload.param();
    // Boolean value = (Boolean) payload.value();
    // log.info("transfer received write MPoint request with param {} and value {}",
    // param, value);
    // return queueService.addRequestToQueue(URGENT, payload, "writeMPoint");
    // }

    // ---------------------------------
    @PostMapping("/writeDPoint")
    public String writeDPoint(@RequestBody RequestWriteDPointDTO payload) {
        long curr = System.currentTimeMillis();
        String param = (String) payload.param();
        Integer value = (Integer) payload.value();
        log.info("transfer received write DPoint request with param {} and value {}", param, value);
        queueService.addRequestToQueue(URGENT, payload, "writeDPoint").whenComplete((res, exp) -> {
            log.info("request write D point complate in " + (System.currentTimeMillis() - curr));
        });
        return new String("ok");
    }

    @GetMapping("getCountMetal")
    public CompletableFuture<Object> getCountMetal() {
        long curr = System.currentTimeMillis();
        log.info("transfer received get count metal request");
        return queueService.addRequestToQueue(IMPORTANT, null, "getCountMetal").whenComplete((res, exp) -> {
            log.info("request get count metal complate in " + (System.currentTimeMillis() - curr));
        });
    }

    @GetMapping("getCountNonMetal")
    public CompletableFuture<Object> getCountNonMetal() {
        long curr = System.currentTimeMillis();
        log.info("transfer received get count non metal request");
        return queueService.addRequestToQueue(IMPORTANT, null, "getCountNonMetal").whenComplete((res, exp) -> {
            log.info("request get count non meatal work pices comopleate in " + (System.currentTimeMillis() - curr));
        });
    }

    @GetMapping("test")
    public CompletableFuture<Object> test() {
        long curr = System.currentTimeMillis();
        log.info("test");
        return queueService.addRequestToQueue(IMPORTANT, null, "test").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("test request done in " + (done - curr));
        });
    }

    @GetMapping("EStop")
    public CompletableFuture<Object> EStop() {
        long curr = System.currentTimeMillis();
        log.info("received n transfer Emergency stop process");
        return queueService.addRequestToQueue(URGENT, null, "EStop").whenComplete((res, exp) -> {
            long done = System.currentTimeMillis();
            log.info("EStop process done in " + (done - curr));
        });
    }

}
