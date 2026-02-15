package com.example.demo.service;

import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.plc.PLCController;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@Service
public class ServicePLC {
    private PLCController plc;
    Dotenv dotenv = Dotenv.load();
    private String plcIP = dotenv.get("PLC_IP");
    private int tcpPort = Integer.parseInt(dotenv.get("TCP_PORT"));
    private static final Logger log = LoggerFactory.getLogger(ServicePLC.class);
    private boolean plcConnected = false;

    @PostConstruct
    public void init() {
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

    public String plcState() {
        try {
            log.info("received plc state request");
            return String.valueOf(plc.readD(plc.getDPoint("STATE")));
        } catch (Exception e) {
            log.error("Error reading PLC state: {}", e.getMessage());
            return "PLC Disconnected: " + e.getMessage();
        }
    }

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

    public String writeDPoint(@RequestBody Map<String, Object> payload) {
        try {

            log.info("Received payload: {}", payload);
            Object deviceObj = payload.get("device");
            if (!(deviceObj instanceof String)) {
                log.warn("Device parameter is not a string: {}", deviceObj);
                return "device Error: must be a string";
            }
            String param = (String) deviceObj;
            if (param.isEmpty() || plc.DdeviceIsEmpty(param)) {
                log.warn("DPoint write: device not found -> {}", param);
                return "NoDevice";
            }

            Object valueObj = payload.get("value");
            int value;

            if (valueObj instanceof Number) {
                value = ((Number) valueObj).intValue();
            } else if (valueObj instanceof String) {
                try {
                    // 如果是字串，嘗試解析成整數
                    value = Integer.parseInt((String) valueObj);
                } catch (NumberFormatException e) {
                    log.warn("Value string is not a valid integer: {}", valueObj);
                    return "value Error: string must be a valid integer";
                }
            } else if (valueObj instanceof Boolean) {
                value = (Boolean) valueObj ? 1 : 0;
            } else {
                log.warn("Value parameter is not a valid type: {}", valueObj);
                return "value Error: must be integer, string, or boolean";
            }
            plc.writeD(plc.getDPoint(param), value);
            log.info("Success DPoint {} set to {}", param, value);
            return "Success: " + param + " set to " + value;

        } catch (Exception e) {
            log.error("Error writing DPoint: {}", e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public boolean isPlcConnected() {
        return plcConnected;
    }

}
