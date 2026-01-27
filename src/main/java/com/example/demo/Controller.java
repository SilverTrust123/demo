package com.example.demo;

import com.example.demo.plc.PLCController;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "*")
@RestController
public class Controller {
    private PLCController plc;
    Dotenv dotenv = Dotenv.load();
    private String plcIP = dotenv.get("PLC_IP");
    private int tcpPort = Integer.parseInt(dotenv.get("TCP_PORT"));

    public Controller() {
        try {
            this.plc = new PLCController(plcIP, tcpPort);
        } catch (Exception e) {
            System.err.println("PLC 連線失敗code: " + e.getMessage());
        }
    }

    @GetMapping("/")
    public String home() {
        return "backend running";
    }

    private Map<String, SensorDataTemperatureAndHumidity> temperatureAndHumidityDataMap = new ConcurrentHashMap<>();

    @PostMapping("/TemparatureAndHumidityData")
    public String receiveData(@RequestBody SensorDataTemperatureAndHumidity data) {
        if (data.getDeviceId() == null) {
            return "Temparature and humidity deviceId is required";
        }
        temperatureAndHumidityDataMap.put(data.getDeviceId(), data);
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

    private Map<String, SensorDataCircuit> circuitDataMap = new ConcurrentHashMap<>();

    @PostMapping("/CircuitData")
    public String receiveCircuitData(@RequestBody SensorDataCircuit data) {
        if (data.getDeviceId() == null || data.getDeviceId().isEmpty()) {
            return "circuit deviceId is required";
        }
        circuitDataMap.put(data.getDeviceId(), data);
        return "OK";
    }

    @GetMapping("/CircuitData")
    public Collection<SensorDataCircuit> getCircuitData() {
        return circuitDataMap.values();
    }

    // 數位雙生：讀取 M 點狀態
    // 回傳值說明：1=ON，0=OFF，3=參數錯誤或沒有設備
    @GetMapping("/plc/MPointState")
    public int MpointState(@RequestParam String param) {
        if (param == null || param.isEmpty() || plc.MdeviceIsEmpty(param)) {
            return 3;
        }
        try {
            return plc.readM(plc.GATE_01) ? 1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 詢問現在參數：讀取 D 點數值
    // NoDevice=沒有設備，Error=讀取錯誤，回傳真的數值)(代驗證)
    @GetMapping("/plc/DPointData")
    public String DPointData(@RequestParam String param) {
        if (param == null || param.isEmpty() || plc.DdeviceIsEmpty(param)) {
            return "NoDevice";
        }
        try {
            int val = plc.readD(plc.getDPoint(param));
            return String.valueOf(val);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error";
    }

    // 前端控制指令：寫入 M 點
    // 收給我("device":"明子","value":true false)
    // Success=成功，NoDevice=沒有設備，Error=寫入錯誤我會給你error馬 value Error=值錯誤
    // 等一下要給董事長devicdid
    @PostMapping("/plc/writeMPoint")
    public String writeMPoint(@RequestBody Map<String, Object> payload) {
        String param = (String) payload.get("device");
        if (param == null || param.isEmpty() || plc.MdeviceIsEmpty(param)) {
            return "NoDevice";
        }
        boolean value = (boolean) payload.get("value");
        if (param == null || param.isEmpty() || (value != false && value != true)) {
            return "value Error";
        }
        try {
            plc.writeM(plc.getMPoint(param), value);
            return "Success: " + param + " set to " + value;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/emerStop")
    public String emerStop(@RequestParam String param) {
        try {
            plc.writeM(plc.EMER_STOP, true);
            return "EMERGENCY STOP ACTIVATED";
        } catch (Exception e) {
            return "Stop Failed";
        }
    }

    @GetMapping("/emerRestort")
    public String emerRestart(@RequestParam String param) {
        try {
            plc.writeM(plc.EMER_STOP, false);
            return "SYSTEM RESTORED";
        } catch (Exception e) {
            return "Restore Failed";
        }
    }
}