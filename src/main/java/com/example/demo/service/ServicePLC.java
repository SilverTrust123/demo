package com.example.demo.service;

import org.slf4j.LoggerFactory;

import java.util.HashMap;
// import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// import com.example.demo.plc.DPoint;
// import com.example.demo.plc.MPoint;
import com.example.demo.plc.PLCDriver;
import com.example.demo.DTO.responseDTO.PLCResponseDTO.*;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class ServicePLC {
    private PLCDriver plc;
    Dotenv dotenv = Dotenv.load();
    private String plcIP = dotenv.get("PLC_IP");
    private int tcpPort = Integer.parseInt(dotenv.get("TCP_PORT"));
    private static final Logger log = LoggerFactory.getLogger(ServicePLC.class);
    private boolean plcConnected = false;

    @Autowired
    private ServiceLog serviceLog;

    @PostConstruct
    public void init() {
        try {
            this.plc = new PLCDriver(plcIP, tcpPort);
            plcConnected = true;
            log.info("PLC connect secces");
            System.out.println("PLC 連線成功");
        } catch (Exception e) {
            log.error("cant connect to plc, error code {}", e.getMessage());
            serviceLog.record("ERROR", "ServicePLC", "cant connect to plc, error code" + e.getMessage());
            System.err.println("PLC 連線失敗 code: " + e.getMessage());
        }
    }

    @PreDestroy
    public void cleanup() {
        if (plc != null) {
            plc.close();
            log.info("PLC connection closed");
            System.out.println("PLC 連線已關閉");
        }
    }

    public ResponsePointState MpointState(String param) {
        log.info("receieved read m point request {} ", param);
        try {
            if (param == null || param.isEmpty()) {
                log.warn("MPointState: param is null or empty");
                serviceLog.record("WARN", "ServicePLC", "MPointState: param is null or empty");
                return new ResponsePointState("MPointState: param is null or empty");
            }

            if (plc.MdeviceIsEmpty(param)) {
                log.warn("MPointState: device not found {} ", param);
                serviceLog.record("WARN", "ServicePLC", "MPointState: device not found " + param);
                return new ResponsePointState("MPointState: device not found " + param);

            }

            boolean state = plc.readM(plc.getMPoint(param));
            log.info("MPoint state {} been {}", param, state);
            return new ResponsePointState("MPointState: " + param + " = " + state);

        } catch (Exception e) {
            log.error("MPointState error param {} with error {}", param, e.getMessage());
            e.printStackTrace();
            return new ResponsePointState("MPointState error, param=" + param);
        }
    }

    public ResponsePointState DPointData(String param) {
        try {
            log.info("received read d point request {} ", param);

            if (param == null || param.isEmpty()) {
                log.warn("DPointData: param is null or empty");
                serviceLog.record("WARN", "ServicePLC", "DPointState: param is null or empty");
                return new ResponsePointState("NoDevice");
            }

            if (plc.DdeviceIsEmpty(param)) {
                log.warn("DPointData: device not found -> {}", param);
                serviceLog.record("WARN", "ServicePLC", "DPointState: device not found " + param);
                return new ResponsePointState("NoDevice");
            }

            int val = plc.readD(plc.getDPoint(param));
            log.info("DPointData: {} = {}", param, val);
            return new ResponsePointState(String.valueOf(val));

        } catch (Exception e) {
            log.error("DPointData error param {} with error {}", param, e.getMessage());
            serviceLog.record("ERROR", "ServicePLC",
                    "DPointData error param " + param + " with error " + e.getMessage());
            e.printStackTrace();
            return new ResponsePointState("Error");
        }
    }

    public ResponseAllDPointStateDTO AllDPointData() throws Exception {
        HashMap<String, Integer> curr = plc.getAllDPoints();
        log.info("received get all DPoint request");
        if (curr.isEmpty()) {
            log.warn("no DPoint data its empty");
            serviceLog.record("ERROR", "ServicePLC", "no DPoint data its empty");
            return new ResponseAllDPointStateDTO(curr);
        }
        log.info("request accept return {} ", curr);
        return new ResponseAllDPointStateDTO(curr);
    }

    public ResponseAllMPointStateDTO AllMPointData() throws Exception {
        HashMap<String, Boolean> curr = plc.getAllMPoints();
        log.info("received get all MPoint request");
        if (curr.isEmpty()) {
            log.warn("no M point data its empty");
            serviceLog.record("WARN", "ServicePLC", "received get all MPoint request");
            return new ResponseAllMPointStateDTO(curr);
        }
        log.info("request accept return {} ", curr);
        return new ResponseAllMPointStateDTO(curr);
    }

    public ResponsePointState plcState() {
        try {
            log.info("received plc state request");
            return new ResponsePointState(String.valueOf(plc.readD(plc.getDPoint("STATE"))));
        } catch (Exception e) {
            log.error("Error reading PLC state: {}", e.getMessage());
            serviceLog.record("ERROR", "ServicePLC", "Error reading PLC state: " + e.getMessage());
            return new ResponsePointState("PLC Disconnected: " + e.getMessage());
        }
    }

    public ResponseCountMetal getCountMetal() {
        try {
            log.info("received get count metal");
            return new ResponseCountMetal(plc.getCountMetal());
        } catch (Exception e) {
            log.error("Error reading get count metal: {}", e.getMessage());
            serviceLog.record("ERROR", "ServicePLC", "Error reading get count metal: " + e.getMessage());
            return new ResponseCountMetal(0);
        }
    }

    public ResponseCountNonMetal getCountNonMetal() {
        try {
            log.info("received get count metal");
            return new ResponseCountNonMetal(plc.getCountNonMetal());
        } catch (Exception e) {
            log.error("Error reading get count non metal: {}", e.getMessage());
            serviceLog.record("ERROR", "ServicePLC", "Error reading get count non metal: " + e.getMessage());
            return new ResponseCountNonMetal(0);
        }
    }

    public String test() {
        return new String("secess");
    }

    // public Map<String, MPoint> getMPointMap() {
    // return plc.getMPointMap();
    // }

    // public Map<String, DPoint> getDPointMap() {
    // return plc.getDPointMap();
    // }

    // try {

    // log.info("Received payload: {}", payload);
    // Object deviceObj = payload.get("device");
    // if (!(deviceObj instanceof String)) {
    // log.warn("Device parameter is not a string: {}", deviceObj);
    // return "device Error: must be a string";
    // }
    // String param = (String) deviceObj;
    // if (param.isEmpty() || plc.MdeviceIsEmpty(param)) {
    // log.warn("MPoint write: device not found -> {}", param);
    // return "NoDevice";
    // }

    // Object valueObj = payload.get("value");
    // boolean value;
    // if (valueObj instanceof Boolean) {
    // value = (Boolean) valueObj;
    // } else if (valueObj instanceof String) {
    // value = Boolean.parseBoolean((String) valueObj);
    // } else if (valueObj instanceof Number) {
    // value = ((Number) valueObj).intValue() != 0;
    // } else {
    // log.warn("Value parameter is not a valid type: {}", valueObj);
    // return "value Error: must be boolean, string, or number";
    // }
    // plc.writeM(plc.getMPoint(param), value);
    // log.info("Success MPoint {} set to {}", param, value);
    // return "Success: " + param + " set to " + value;

    // } catch (Exception e) {
    // log.error("Error writing MPoint: {}", e.getMessage());
    // e.printStackTrace();
    // return "Error: " + e.getMessage();
    // }
    public ResponsePointState writeMPoint(String param, Boolean value) {
        try {
            plc.writeM(plc.getMPoint(param), value);
            log.info("Success MPoint {} set to {}", param, value);
            return new ResponsePointState("Success: " + param + " set to " + value);
        } catch (Exception e) {
            log.error("Error writing MPoint: {}", e.getMessage());
            e.printStackTrace();
            return new ResponsePointState("Error: " + e.getMessage());
        }
    }

    public ResponsePointState writeDPoint(String param, Integer value) {
        try {
            plc.writeD(plc.getDPoint(param), value);
            log.info("Success DPoint {} set to {}", param, value);
            return new ResponsePointState("Success: " + param + " set to " + value);

        } catch (Exception e) {
            log.error("Error writing DPoint: {}", e.getMessage());
            e.printStackTrace();
            return new ResponsePointState("Error: " + e.getMessage());
        }
    }

    public boolean isPlcConnected() {
        return plcConnected;
    }

    public ResponsePointState EStop() {
        try {
            plc.writeM(plc.getMPoint("EStop"), true);
            log.info("production line shot down");
            return new ResponsePointState("process stop");

        } catch (Exception e) {
            log.error("Shot down Error", e.getMessage());
            e.printStackTrace();
            return new ResponsePointState("Shot down Error: " + e.getMessage());
        }
    }

}
