package com.example.demo;

import com.example.demo.cam.SensorDataCam;
import com.example.demo.plc.PLCController;
import com.example.demo.sensor.SensorDataAirParticulates;
import com.example.demo.sensor.SensorDataAirQuality;
import com.example.demo.sensor.SensorDataCircuit;
import com.example.demo.sensor.SensorDataTemperatureAndHumidity;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import io.github.cdimascio.dotenv.Dotenv;

@CrossOrigin(origins = "*")
@RestController
public class Controller {
    private PLCController plc;
    Dotenv dotenv = Dotenv.load();
    private String plcIP = dotenv.get("PLC_IP");
    private int tcpPort = Integer.parseInt(dotenv.get("TCP_PORT"));
    private boolean plcConnected = false;
    private final int WINDOW_SIZE = 10;

    public Controller() {
        try {
            this.plc = new PLCController(plcIP, tcpPort);
            plcConnected = true;
            System.out.println("PLC 連線成功");
        } catch (Exception e) {
            System.err.println("PLC 連線失敗 code: " + e.getMessage());
        }
    }

    @GetMapping("/")
    public String home() {
        return "backend running";
    }

    @GetMapping("/PLCConnect")
    public String PLCConnect() {
        return "PLC Connected: " + plcConnected;
    }
    // 原來的沒有sliding window平滑的
    // @PostMapping("/TemparatureAndHumidityData")
    // public String receiveData(@RequestBody SensorDataTemperatureAndHumidity data)
    // {
    // if (data.getDeviceId() == null) {
    // return "Temparature and humidity deviceId is required";
    // }
    // temperatureAndHumidityDataMap.put(data.getDeviceId(), data);
    // return "OK";
    // }

    private Map<String, SensorDataTemperatureAndHumidity> temperatureAndHumidityDataMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Deque<Double>> tempHistoryMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Deque<Double>> humidHistoryMap = new ConcurrentHashMap<>();

    @PostMapping("/TemparatureAndHumidityData")
    public String receiveData(@RequestBody SensorDataTemperatureAndHumidity data) {
        if (data.getDeviceId() == null) {
            return "Temparature and humidity deviceId is required";
        }
        String deviceId = data.getDeviceId();
        double rawTemp = data.getTemperature();
        float smoothTemp = calculateAverage(tempHistoryMap, deviceId, rawTemp);
        data.setTemperature(smoothTemp);
        double rawHumid = data.getHumidity();
        float smoothHumid = calculateAverage(humidHistoryMap, deviceId, rawHumid);
        data.setHumidity(smoothHumid);
        temperatureAndHumidityDataMap.put(deviceId, data);

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

    private final ConcurrentHashMap<String, Deque<Double>> circuitHistoryMap = new ConcurrentHashMap<>();

    @PostMapping("/CircuitData")
    public String receiveCircuitData(@RequestBody SensorDataCircuit data) {
        if (data.getDeviceId() == null || data.getDeviceId().isEmpty()) {
            return "circuit deviceId is required";
        }
        // historyMap, String deviceId,double newValue
        String deviceId = data.getDeviceId();
        float rawVol = data.getVoltage();
        float smoothVol = calculateAverage(circuitHistoryMap, deviceId, (double) rawVol);
        data.setVoltage(smoothVol);

        circuitDataMap.put(deviceId, data);

        return "OK";
    }

    @GetMapping("/CircuitData")
    public Collection<SensorDataCircuit> getCircuitData() {
        return circuitDataMap.values();
    }

    @GetMapping("/CircuitData/{deviceId}")
    public SensorDataCircuit getCircuitDataById(@PathVariable String deviceId) {
        return circuitDataMap.get(deviceId);
    }

    private Map<String, SensorDataAirQuality> airQualityDataMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Deque<Double>> airQualityHistoryMap = new ConcurrentHashMap<>();

    @PostMapping("/AirQualityData")
    public String recriveAirQuality(@RequestBody SensorDataAirQuality data) {
        if (data.getDeviceId() == null || data.getDeviceId().isEmpty()) {
            return "air quality deviceId is required";
        }
        String deviceId = data.getDeviceId();
        float rawAQ = data.getAirPollution();
        float smoothAQ = calculateAverage(airQualityHistoryMap, deviceId, rawAQ);
        data.setAirPollution(smoothAQ);
        airQualityDataMap.put(deviceId, data);
        return "OK";
    }

    @GetMapping("/AirQualityData")
    public Collection<SensorDataAirQuality> getAirQualityData() {
        return airQualityDataMap.values();
    }

    @GetMapping("/AirQualityData/{deviceId}")
    public SensorDataAirQuality getAirQualityDataById(@PathVariable String deviceId) {
        return airQualityDataMap.get(deviceId);
    }

    private Map<String, SensorDataAirParticulates> airParticulatesDataMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Deque<Double>> airParticulatesHistoryMap = new ConcurrentHashMap<>();

    @PostMapping("/AirParticalData")
    public String recriveAirPartical(@RequestBody SensorDataAirParticulates data) {
        if (data.getDeviceId() == null || data.getDeviceId().isEmpty()) {
            return "air particulates deviceId is required";
        }
        String deviceId = data.getDeviceId();
        float rawAP = data.getPm2_5();
        float smoothAP = calculateAverage(airParticulatesHistoryMap, deviceId, rawAP);
        data.setPm2_5(smoothAP);
        data.setTimestamp((int) (System.currentTimeMillis() / 1000L));
        airParticulatesDataMap.put(deviceId, data);
        return "OK";
    }

    @GetMapping("/AirParticalData")
    public Collection<SensorDataAirParticulates> getAirParticalData() {
        String curr_id = "null";
        for (String id : airParticulatesDataMap.keySet()) {
            curr_id = id;
            SensorDataAirParticulates data = airParticulatesDataMap.get(curr_id);
            int now = (int) (System.currentTimeMillis() / 1000L);
            int gap = now - data.getTimestamp();
            if (gap > 60) {
                return null;
            }
        }
        return airParticulatesDataMap.values();
    }

    @GetMapping("/AirParticalData/{deviceId}")
    public SensorDataAirParticulates getAirParticalDataById(@PathVariable String deviceId) {
        SensorDataAirParticulates data = airParticulatesDataMap.get(deviceId);
        if (data == null) {
            return null;
        }
        int now = (int) (System.currentTimeMillis() / 1000L);
        int gap = now - data.getTimestamp();
        if (gap > 60) {
            return null;
        }
        return data;
    }

    private Map<String, SensorDataCam> camDataMap = new ConcurrentHashMap<>();

    @PostMapping("/CamData")
    public String receiveCamData(@RequestBody SensorDataCam data) {

        if (data.getDeviceId() == null) {
            return "Cam deviceId is required";
        }

        camDataMap.put(data.getDeviceId(), data);
        return "OK";
    }

    @GetMapping("/CamData/{deviceId}")
    public SensorDataCam getCamData(@PathVariable String deviceId) {
        return camDataMap.get(deviceId);
    }

    @GetMapping("/CamData")
    public Collection<SensorDataCam> getAllCamData() {
        return camDataMap.values();
    }

    // 數位雙生：讀取 M 點狀態
    // 回傳：NoDevice / Error / 真偽值
    @GetMapping("/plc/MPointState")
    public String MpointState(@RequestParam(required = false) String param) {
        try {
            System.out.println("MPointState param = " + param);

            if (param == null || param.isEmpty()) {
                System.out.println("MPointState: param is null or empty");
                return "MPointState: param is null or empty";
            }

            if (plc.MdeviceIsEmpty(param)) {
                System.out.println("MPointState: device not found -> " + param);
                return "MPointState: device not found -> " + param;
            }

            boolean state = plc.readM(plc.getMPoint(param));
            System.out.println("MPointState: " + param + " = " + state);

            return "MPointState: " + param + " = " + state;

        } catch (Exception e) {
            System.err.println("MPointState error, param=" + param);
            e.printStackTrace();
            return "MPointState error, param=" + param;
        }
    }

    // 詢問現在參數：讀取 D 點數值
    // 回傳：NoDevice / Error / 實際數值
    @GetMapping("/plc/DPointData")
    public String DPointData(@RequestParam(required = false) String param) {
        try {
            System.out.println("DPointData param = " + param);

            if (param == null || param.isEmpty()) {
                System.out.println("DPointData: param is null or empty");
                return "NoDevice";
            }

            if (plc.DdeviceIsEmpty(param)) {
                System.out.println("DPointData: device not found -> " + param);
                return "NoDevice";
            }

            int val = plc.readD(plc.getDPoint(param));
            System.out.println("DPointData: " + param + " = " + val);

            return String.valueOf(val);

        } catch (Exception e) {
            System.err.println("DPointData error, param=" + param);
            e.printStackTrace();
            return "Error";
        }
    }

    @GetMapping("/plc/state")
    public String plcState() {
        try {
            return String.valueOf(plc.readD(plc.getDPoint("STATE")));
        } catch (Exception e) {
            return "PLC Disconnected: " + e.getMessage();
        }
    }

    @PostMapping("/plc/writeMPoint")
    public String writeMPoint(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("Received payload: " + payload);
            Object deviceObj = payload.get("device");
            if (!(deviceObj instanceof String)) {
                return "device Error: must be a string";
            }
            String param = (String) deviceObj;
            if (param.isEmpty() || plc.MdeviceIsEmpty(param)) {
                return "NoDevice";
            }
            Object valueObj = payload.get("value");
            boolean value;
            if (valueObj instanceof Boolean) {
                value = (Boolean) valueObj;
            } else if (valueObj instanceof String) {
                value = Boolean.parseBoolean((String) valueObj);
            } else if (valueObj instanceof Number) {
                value = ((Number) valueObj).intValue() != 0;
            } else {
                return "value Error: must be boolean, string, or number";
            }
            System.out.println("Device: " + param + ", Value: " + value);
            plc.writeM(plc.getMPoint(param), value);
            return "Success: " + param + " set to " + value;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // silaing window 平滑數據 就可以消除一些突發的雜訊阿之類的
    private float calculateAverage(ConcurrentHashMap<String, Deque<Double>> historyMap, String deviceId,
            double newValue) {
        Deque<Double> window = historyMap.computeIfAbsent(deviceId, k -> new ArrayDeque<>());

        synchronized (window) {
            if (window.size() >= WINDOW_SIZE) {
                window.pollFirst();
            }
            window.addLast(newValue);

            return (float) window.stream().mapToDouble(Double::doubleValue).average().orElse(newValue);
        }
    }

}
// 前端控制指令：寫入 M 點
// 收給我("device":"名子","value":true false)
// Success=成功，NoDevice=沒有設備，Error=寫入錯誤我會給你error馬 value Error=值錯誤
// 等一下要給董事長devicdid
// @PostMapping("/plc/writeMPoint")
// public String writeMPoint(@RequestBody Map<String, Object> payload) {
// String param = (String) payload.get("device");
// if (param == null || param.isEmpty() || plc.MdeviceIsEmpty(param)) {
// return "NoDevice";
// }
// boolean value = (boolean) payload.get("value");
// if (param == null || param.isEmpty() || (value != false && value != true)) {
// return "value Error";
// }
// try {
// plc.writeM(plc.getMPoint(param), value);
// return "Success: " + param + " set to " + value;
// } catch (Exception e) {
// return "Error: " + e.getMessage();
// }
// }

// @GetMapping("/emerStop")
// public String emerStop(@RequestParam String param) {
// try {
// plc.writeM(plc.EMER_STOP, true);
// return "EMERGENCY STOP ACTIVATED";
// } catch (Exception e) {
// return "Stop Failed";
// }
// }

// @GetMapping("/emerRestort")
// public String emerRestart(@RequestParam String param) {
// try {
// plc.writeM(plc.EMER_STOP, false);
// return "SYSTEM RESTORED";
// } catch (Exception e) {
// return "Restore Failed";
// }
// }