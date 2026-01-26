package com.example.demo;

// import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "*")
@RestController
public class Controller {

    private Map<String, SensorDataTemperatureAndHumidity> temperatureAndHumidityDataMap = new ConcurrentHashMap<>();

    @PostMapping("/TemparatureAndHumidityData")
    public String receiveData(@RequestBody SensorDataTemperatureAndHumidity data) {
        if (data.getDeviceId() == null) {
            return "Temparature and humidity deviceId is required";
        }

        temperatureAndHumidityDataMap.put(data.getDeviceId(), data);
        System.out.println("Received from " + data.getDeviceId() + ": " + data);

        return "OK";
    }

    @GetMapping("/TemparatureAndHumidityData/{deviceId}")
    public SensorDataTemperatureAndHumidity getData(@PathVariable String deviceId) {
        return temperatureAndHumidityDataMap.get(deviceId);
    }

    @GetMapping("/TemparatureAndHumidityData")
    public Collection<SensorDataTemperatureAndHumidity> getAllData() {
        return temperatureAndHumidityDataMap.values();
    }

    @GetMapping("/")
    public String home() {
        return "backend running";
    }

    private Map<String, SensorDataCircuit> circuitDataMap = new ConcurrentHashMap<>();

    // 傳電流資料 String
    // @PostMapping("/circultData")
    // public String recieveCirData(@RequestBody SensorDataCircuit data) {
    // if (data.getDeviceId() == null) {
    // return "Circult deviceId is required";
    // }

    // circuitDataMap.put(data.getDeviceId(), data);
    // System.out.println("Received from " + data.getDeviceId() + ": " + data);

    // return "OK";
    // }

    // @GetMapping("/circuitData")
    // public Collection<SensorDataCircuit> cir_data() {
    // return circuitDataMap.values();
    // }

    @PostMapping("/circuitData")
    public String receiveCircuitData(@RequestBody SensorDataCircuit data) {

        if (data.getDeviceId() == null || data.getDeviceId().isEmpty()) {
            return "circuit deviceId is required";
        }

        circuitDataMap.put(data.getDeviceId(), data);

        System.out.println(
                "Received circuit data from " + data.getDeviceId() + " : " + data);

        return "OK";
    }

    @GetMapping("/circuitData")
    public Collection<SensorDataCircuit> getCircuitData() {
        return circuitDataMap.values();
    }

    // 數位雙生 json 每一個閥門現在的狀態 0->關 1->開
    // "name":gate編號 , "state":0或1
    @GetMapping("/current_data")
    public int dt_data(@RequestParam String param) {
        return 0;
        // return new SensorDataCircuit();
    }

    // 詢問現在參數 json
    // "name":參數名稱(編號) , "value":參數值"
    @GetMapping("/para_data")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    // 前端傳來的控制閥門指令 傳來動哪一個閘門 數據變成怎樣
    // "name":gate編號 , "value":數值
    // 丟回前端確認
    // "name":gate編號 , "value":數值
    @PostMapping("/m_control")
    public String postMethodName(@RequestBody String entity) {
        // TODO: process POST request

        return entity;
    }
}

// sensor 1 is temperature and humidity device
// sensor 2 is circuit device