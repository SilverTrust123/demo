package com.example.demo;

import com.example.demo.cam.SensorDataCam;
import com.example.demo.plc.PLCController;
import com.example.demo.sensor.SensorDataAirParticulates;
import com.example.demo.sensor.SensorDataAirQuality;
import com.example.demo.sensor.SensorDataCircuit;
import com.example.demo.sensor.SensorDataTemperatureAndHumidity;
import com.example.demo.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "*")
@RestController
public class Controller {
    private PLCController plc;
    Dotenv dotenv = Dotenv.load();
    private String plcIP = dotenv.get("PLC_IP");
    private int tcpPort = Integer.parseInt(dotenv.get("TCP_PORT"));
    private boolean plcConnected = false;
    private final int WINDOW_SIZE = 10;
    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private ServiceTemparatureAndHumidity serviceTemparatureAndHumidity;

    public Controller() {
        log.info("ServiceTemparatureAndHumidity injected: {}", serviceTemparatureAndHumidity);

        try {
            this.plc = new PLCController(plcIP, tcpPort);
            plcConnected = true;
            log.info("PLC connect secces");
            System.out.println("PLC 連線成功");
        } catch (Exception e) {
            log.error("cant connect to plc, error code", e.getMessage());
            System.err.println("PLC 連線失敗 code: " + e.getMessage());
        }
    }

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

    private Map<String, SensorDataCircuit> circuitDataMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Deque<Double>> circuitHistoryMap = new ConcurrentHashMap<>();

    @PostMapping("/CircuitData")
    public String receiveCircuitData(@RequestBody SensorDataCircuit data) {
        if (data.getDeviceId() == null || data.getDeviceId().isEmpty()) {
            log.info("circuit data is required follow by detail", data);
            return "circuit deviceId is required";
        }
        // historyMap, String deviceId,double newValue
        data.setTimestamp((int) (System.currentTimeMillis() / 1000L));
        String deviceId = data.getDeviceId();
        float rawVol = data.getVoltage();
        float smoothVol = calculateAverage(circuitHistoryMap, deviceId, (double) rawVol);
        data.setVoltage(smoothVol);
        circuitDataMap.put(deviceId, data);
        log.info("{}put in ok", deviceId);
        return "OK";
    }

    // 船空的回去就是找不到東西 或是太舊了
    @GetMapping("/CircuitData/{deviceId}")
    public SensorDataCircuit getCircuitData(@PathVariable String deviceId) {
        SensorDataCircuit ans = circuitDataMap.get(deviceId);
        if (ans != null && isTimeValid(ans)) {
            log.info("Received request single device {} and return detail {}", deviceId, ans);
            return ans;
        }
        log.warn("Cannot find valid data for device name {}", deviceId);
        return new SensorDataCircuit();
    }

    // 這個本來最多就一個 如果船空的回去就是太舊了
    @GetMapping("/CircuitData")
    public Collection<SensorDataCircuit> getAllCircuitData() {
        Collection<SensorDataCircuit> ans = circuitDataMap.values()
                .stream()
                .filter(this::isTimeValid)
                .toList();
        log.info("received all circuit data request detail {} ", ans);
        return ans;
    }

    // 等一下要確認一下每一個都有timesteamp
    private Map<String, SensorDataAirQuality> airQualityDataMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Deque<Double>> airQualityHistoryMap = new ConcurrentHashMap<>();

    @PostMapping("/AirQualityData")
    public String recriveAirQuality(@RequestBody SensorDataAirQuality data) {
        if (data.getDeviceId() == null || data.getDeviceId().isEmpty()) {
            log.info("air quality deviceId is required detaild by follow {} ", data);
            return "air quality deviceId is required";
        }
        data.setTimestamp((int) (System.currentTimeMillis() / 1000L));
        String deviceId = data.getDeviceId();
        float rawAQ = data.getAirPollution();
        float smoothAQ = calculateAverage(airQualityHistoryMap, deviceId, rawAQ);
        data.setAirPollution(smoothAQ);
        airQualityDataMap.put(deviceId, data);
        log.info("{} put in ok", deviceId);
        return "OK";
    }

    @GetMapping("/AirQualityData/{deviceId}")
    public SensorDataAirQuality getAirQualityData(@PathVariable String deviceId) {
        SensorDataAirQuality ans = airQualityDataMap.get(deviceId);
        if (ans != null && isTimeValid(ans)) {
            log.info("Received request single device {} and return detail {}", deviceId, ans);
            return ans;
        }
        log.warn("Cannot find valid data for device name {}", deviceId);
        return new SensorDataAirQuality();
    }

    @GetMapping("/AirQualityData")
    public Collection<SensorDataAirQuality> getAllAirQualityData() {
        Collection<SensorDataAirQuality> ans = airQualityDataMap.values()
                .stream()
                .filter(this::isTimeValid)
                .toList();
        log.info("received all air quality data request detail {} ", ans);
        return ans;
    }

    private Map<String, SensorDataAirParticulates> airParticulatesDataMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Deque<Double>> airParticulatesHistoryMap = new ConcurrentHashMap<>();

    @PostMapping("/AirParticalData")
    public String recriveAirPartical(@RequestBody SensorDataAirParticulates data) {
        if (data.getDeviceId() == null || data.getDeviceId().isEmpty()) {
            log.info("air particulates deviceId is required detaild by follow {} ", data);
            return "air particulates deviceId is required";
        }
        String deviceId = data.getDeviceId();
        float rawAP = data.getPm2_5();
        float smoothAP = calculateAverage(airParticulatesHistoryMap, deviceId, rawAP);
        data.setPm2_5(smoothAP);
        data.setTimestamp((int) (System.currentTimeMillis() / 1000L));
        airParticulatesDataMap.put(deviceId, data);
        log.info("{} put in ok", deviceId);
        return "OK";
    }

    @GetMapping("/AirParticalData/{deviceId}")
    public SensorDataAirParticulates getAirParticalDataById(@PathVariable String deviceId) {
        SensorDataAirParticulates ans = airParticulatesDataMap.get(deviceId);
        if (ans != null && isTimeValid(ans)) {
            log.info("Received request single device {} and return detail {}", deviceId, ans);
            return ans;
        }
        log.warn("Cannot find valid data for device name {}", deviceId);
        return new SensorDataAirParticulates();
    }

    @GetMapping("/AirParticalData")
    public Collection<SensorDataAirParticulates> getAirParticalData() {
        Collection<SensorDataAirParticulates> ans = airParticulatesDataMap.values()
                .stream()
                .filter(this::isTimeValid)
                .toList();
        log.info("received all air particulates data request detail {} ", ans);
        return ans;
    }

    private Map<String, SensorDataCam> camDataMap = new ConcurrentHashMap<>();

    @PostMapping("/CamData")
    public String receiveCamData(@RequestBody SensorDataCam data) {
        String deviceId = data.getDeviceId();
        if (deviceId == null) {
            log.info("received cam data from {} ", deviceId);
            return "Cam deviceId is required";
        }
        data.setTimestamp((int) (System.currentTimeMillis() / 1000L));
        camDataMap.put(deviceId, data);
        log.info("{} put in ok", deviceId);
        return "OK";
    }

    @GetMapping("/CamData/{deviceId}")
    public SensorDataCam getCamData(@PathVariable String deviceId) {
        SensorDataCam ans = camDataMap.get(deviceId);
        if (ans != null && isTimeValid(ans)) {
            log.info("Received request single device {} and return detail {}", deviceId, ans);
            return ans;
        }
        log.warn("Cannot find valid data for device name {}", deviceId);
        return new SensorDataCam();
    }

    @GetMapping("/CamData")
    public Collection<SensorDataCam> getAllCamData() {
        Collection<SensorDataCam> ans = camDataMap.values()
                .stream()
                .filter(this::isTimeValid)
                .toList();
        log.info("received all cam data request detail {} ", ans);
        return ans;
    }

    // 數位雙生：讀取 M 點狀態
    // 回傳：NoDevice / Error / 真偽值
    @GetMapping("/plc/MPointState")
    public String MpointState(@RequestParam(required = false) String param) {
        log.info("receieved read m point request {} ", param);
        try {
            if (param == null || param.isEmpty()) {
                log.warn("MPointState: param is null or empty");
                return "MPointState: param is null or empty";
            }

            if (plc.MdeviceIsEmpty(param)) {
                log.warn("MPointState: device not found {} ", param);
                return "MPointState: device not found " + param;
            }

            boolean state = plc.readM(plc.getMPoint(param));
            log.info("MPoint state {} been {}", param, state);
            return "MPointState: " + param + " = " + state;

        } catch (Exception e) {
            log.error("MPointState error param {} with error {}", param, e.getMessage());
            e.printStackTrace();
            return "MPointState error, param=" + param;
        }
    }

    @GetMapping("/plc/AllDPointData")
    public Map<String, Integer> AllDPointData() throws Exception {
        log.info("received get all DPoint request");
        if (plc.getAllDPoints().isEmpty()) {
            log.warn("no DPoint data its empty");
            return new HashMap<>();
        } else if (plc.getAllDPoints() == null) {
            log.warn("its null");
            return new HashMap<>();
        }
        log.info("request accept return {} ", plc.getAllDPoints());
        return plc.getAllDPoints();
    }

    @GetMapping("/plc/AllMPointData")
    public Map<String, Boolean> AllMPointData() throws Exception {
        log.info("received get all MPoint request");
        if (plc.getAllMPoints().isEmpty()) {
            log.warn("no M point data its empty");
            return new HashMap<>();
        } else if (plc.getAllMPoints() == null) {
            log.warn("its null");
            return new HashMap<>();
        }
        log.info("request accept return {} ", plc.getAllMPoints());
        return plc.getAllMPoints();
    }

    // 詢問現在參數：讀取 D 點數值
    // 回傳：NoDevice / Error / 實際數值
    @GetMapping("/plc/DPointData")
    public String DPointData(@RequestParam(required = false) String param) {
        try {
            log.info("received read d point request {} ", param);

            if (param == null || param.isEmpty()) {
                log.warn("DPointData: param is null or empty");
                return "NoDevice";
            }

            if (plc.DdeviceIsEmpty(param)) {
                log.warn("DPointData: device not found -> {}", param);
                return "NoDevice";
            }

            int val = plc.readD(plc.getDPoint(param));
            log.info("DPointData: {} = {}", param, val);

            return String.valueOf(val);

        } catch (Exception e) {
            log.error("DPointData error param {} with error {}", param, e.getMessage());
            e.printStackTrace();
            return "Error";
        }
    }

    @GetMapping("/plc/state")
    public String plcState() {
        try {
            log.info("received plc state request");
            return String.valueOf(plc.readD(plc.getDPoint("STATE")));
        } catch (Exception e) {
            log.error("Error reading PLC state: {}", e.getMessage());
            return "PLC Disconnected: " + e.getMessage();
        }
    }

    @PostMapping("/plc/writeMPoint")
    public String writeMPoint(@RequestBody Map<String, Object> payload) {
        try {

            log.info("Received payload: {}", payload);
            Object deviceObj = payload.get("device");
            if (!(deviceObj instanceof String)) {
                log.warn("Device parameter is not a string: {}", deviceObj);
                return "device Error: must be a string";
            }
            String param = (String) deviceObj;
            if (param.isEmpty() || plc.MdeviceIsEmpty(param)) {
                log.warn("MPoint write: device not found -> {}", param);
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
                log.warn("Value parameter is not a valid type: {}", valueObj);
                return "value Error: must be boolean, string, or number";
            }
            plc.writeM(plc.getMPoint(param), value);
            log.info("Success MPoint {} set to {}", param, value);
            return "Success: " + param + " set to " + value;

        } catch (Exception e) {
            log.error("Error writing MPoint: {}", e.getMessage());
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
            log.info("Device {} new value: {}, history: {}", deviceId, newValue, window);
            return (float) window.stream().mapToDouble(Double::doubleValue).average().orElse(newValue);
        }
    }

    ConcurrentHashMap<String, Object> magicData = new ConcurrentHashMap<>();

    @GetMapping("/AllData")
    public ConcurrentHashMap<String, Object> AllData(@RequestParam String param) throws Exception {
        // magicData.put("temperatureAndHumidityDataMap",
        // temperatureAndHumidityDataMap);
        magicData.put("circuitDataMap", circuitDataMap);
        magicData.put("airQualityDataMap", airQualityDataMap);
        magicData.put("airParticulatesDataMap", airParticulatesDataMap);
        magicData.put("camDataMap", camDataMap);
        magicData.put("DPoint", plc.getAllDPoints());
        magicData.put("MPoint", plc.getAllMPoints());
        log.info("received all data request with param {} and return {}", param, magicData);
        return magicData;
    }

    private boolean isTimeValid(SensorDataCircuit data) {
        if (data == null) {
            return false;
        }
        int now = (int) (System.currentTimeMillis() / 1000L);
        int gap = now - data.getTimestamp();

        if (gap > 60) {
            log.warn("Data of device {} is too old, timestamp {}, now {}",
                    data.getDeviceId(), data.getTimestamp(), now);
            return false;
        }
        return true;
    }

    private boolean isTimeValid(SensorDataAirQuality data) {
        if (data == null) {
            return false;
        }
        int now = (int) (System.currentTimeMillis() / 1000L);
        int gap = now - data.getTimestamp();

        if (gap > 60) {
            log.warn("Data of device {} is too old, timestamp {}, now {}",
                    data.getDeviceId(), data.getTimestamp(), now);
            return false;
        }
        return true;
    }

    private boolean isTimeValid(SensorDataAirParticulates data) {
        if (data == null) {
            return false;
        }
        int now = (int) (System.currentTimeMillis() / 1000L);
        int gap = now - data.getTimestamp();

        if (gap > 60) {
            log.warn("Data of device {} is too old, timestamp {}, now {}",
                    data.getDeviceId(), data.getTimestamp(), now);
            return false;
        }
        return true;
    }

    private boolean isTimeValid(SensorDataCam data) {
        if (data == null) {
            return false;
        }
        int now = (int) (System.currentTimeMillis() / 1000L);
        int gap = now - data.getTimestamp();

        if (gap > 60) {
            log.warn("Data of device {} is too old, timestamp {}, now {}",
                    data.getDeviceId(), data.getTimestamp(), now);
            return false;
        }
        return true;
    }

}
