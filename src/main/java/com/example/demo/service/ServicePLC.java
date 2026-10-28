package com.example.demo.service;

import org.slf4j.LoggerFactory;

import java.util.HashMap;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private volatile boolean plcConnected = false;

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
        // checkOrReconnectPLC();
        log.info("receieved read m point request {} ", param);
        if (!isPlcConnected()) {
            return new ResponsePointState("PLC is disconnected");
        }
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
            plcConnected = false;
            log.error("MPointState error param {} with error {}", param, e.getMessage());
            e.printStackTrace();
            return new ResponsePointState("MPointState error, param=" + param);
        }
    }

    public ResponsePointState DPointData(String param) {
        // checkOrReconnectPLC();
        if (!isPlcConnected()) {
            return new ResponsePointState("PLC is disconnected");
        }
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
            plcConnected = false;
            log.error("DPointData error param {} with error {}", param, e.getMessage());
            serviceLog.record("ERROR", "ServicePLC",
                    "DPointData error param " + param + " with error " + e.getMessage());
            e.printStackTrace();
            return new ResponsePointState("Error");
        }
    }

    public ResponseAllDPointStateDTO AllDPointData() {
        if (!isPlcConnected()) {
            return new ResponseAllDPointStateDTO(new HashMap<>());
        }

        try {
            HashMap<String, Integer> curr = plc.getAllDPoints();
            log.info("received get all DPoint request");

            if (curr.isEmpty()) {
                log.warn("no DPoint data its empty");
                serviceLog.record("WARN", "ServicePLC", "no DPoint data its empty"); // 建議這裡改成 WARN 或保持原樣
                return new ResponseAllDPointStateDTO(curr);
            }

            log.info("request accept return {} ", curr);
            return new ResponseAllDPointStateDTO(curr);

        } catch (Exception e) {
            plcConnected = false;
            log.error("AllDPointData error with error {}", e.getMessage());
            serviceLog.record("ERROR", "ServicePLC", "AllDPointData error: " + e.getMessage());
            return new ResponseAllDPointStateDTO(new HashMap<>());
        }
    }

    public ResponseAllMPointStateDTO AllMPointData() {
        // checkOrReconnectPLC();
        if (!isPlcConnected()) {
            return new ResponseAllMPointStateDTO(new HashMap<>());
        }
        try {

            HashMap<String, Boolean> curr = plc.getAllMPoints();
            log.info("received get all MPoint request");
            if (curr.isEmpty()) {
                log.warn("no M point data its empty");
                serviceLog.record("WARN", "ServicePLC", "received get all MPoint request");
                return new ResponseAllMPointStateDTO(curr);
            }
            log.info("request accept return {} ", curr);
            return new ResponseAllMPointStateDTO(curr);
        } catch (Exception e) {
            plcConnected = false;
            log.error("AllDPointData error with error {}", e.getMessage());
            serviceLog.record("ERROR", "ServicePLC", "AllDPointData error: " + e.getMessage());
            return new ResponseAllMPointStateDTO(new HashMap<>());
        }
    }

    public ResponsePointState plcState() {
        // checkOrReconnectPLC();
        if (!isPlcConnected()) {
            return new ResponsePointState("PLC is disconnected");
        }
        try {
            log.info("received plc state request");
            return new ResponsePointState(String.valueOf(plc.readD(plc.getDPoint("STATE"))));
        } catch (Exception e) {
            plcConnected = false;
            log.error("Error reading PLC state: {}", e.getMessage());
            serviceLog.record("ERROR", "ServicePLC", "Error reading PLC state: " + e.getMessage());
            return new ResponsePointState("PLC Disconnected: " + e.getMessage());
        }
    }

    public ResponseCountMetal getCountMetal() {
        // checkOrReconnectPLC();
        if (!isPlcConnected()) {
            return new ResponseCountMetal(-1);
        }
        try {
            log.info("received get count metal");
            return new ResponseCountMetal(plc.getCountMetal());
        } catch (Exception e) {
            plcConnected = false;
            log.error("Error reading get count metal: {}", e.getMessage());
            serviceLog.record("ERROR", "ServicePLC", "Error reading get count metal: " + e.getMessage());
            return new ResponseCountMetal(0);
        }
    }

    public ResponseCountNonMetal getCountNonMetal() {
        // checkOrReconnectPLC();
        if (!isPlcConnected()) {
            return new ResponseCountNonMetal(-1);
        }
        try {
            log.info("received get count metal");
            return new ResponseCountNonMetal(plc.getCountNonMetal());
        } catch (Exception e) {
            plcConnected = false;
            log.error("Error reading get count non metal: {}", e.getMessage());
            serviceLog.record("ERROR", "ServicePLC", "Error reading get count non metal: " + e.getMessage());
            return new ResponseCountNonMetal(0);
        }
    }

    public String test() {
        return new String("secess");
    }

    public ResponsePointState writeMPoint(String param, Boolean value) {
        // checkOrReconnectPLC();
        if (!isPlcConnected()) {
            return new ResponsePointState("PLC is disconnected");
        }
        try {
            plc.writeM(plc.getMPoint(param), value);
            log.info("Success MPoint {} set to {}", param, value);
            return new ResponsePointState("Success: " + param + " set to " + value);
        } catch (Exception e) {
            plcConnected = false;
            log.error("Error writing MPoint: {}", e.getMessage());
            e.printStackTrace();
            return new ResponsePointState("Error: " + e.getMessage());
        }
    }

    public ResponsePointState writeDPoint(String param, Integer value) {
        // checkOrReconnectPLC();
        if (!isPlcConnected()) {
            return new ResponsePointState("PLC is disconnected");
        }
        try {
            plc.writeD(plc.getDPoint(param), value);
            log.info("Success DPoint {} set to {}", param, value);
            return new ResponsePointState("Success: " + param + " set to " + value);

        } catch (Exception e) {
            plcConnected = false;
            log.error("Error writing DPoint: {}", e.getMessage());
            e.printStackTrace();
            return new ResponsePointState("Error: " + e.getMessage());
        }
    }

    public boolean isPlcConnected() {
        return plcConnected;
    }

    public ResponsePointState EStop() {
        // checkOrReconnectPLC();
        if (!isPlcConnected()) {
            return new ResponsePointState("PLC is disconnected");
        }
        try {
            plc.writeM(plc.getMPoint("EStop"), true);
            log.info("production line shot down");
            return new ResponsePointState("process stop");

        } catch (Exception e) {
            plcConnected = false;
            log.error("Shot down Error", e.getMessage());
            e.printStackTrace();
            return new ResponsePointState("Shot down Error: " + e.getMessage());
        }
    }

    public ResponsePointState EndEStop() {
        // checkOrReconnectPLC();
        if (!isPlcConnected()) {
            return new ResponsePointState("PLC is disconnected");
        }
        try {
            plc.writeM(plc.getMPoint("EndEStop"), true);
            log.info("production line ending shot down");
            return new ResponsePointState("process stop end");

        } catch (Exception e) {
            plcConnected = false;
            log.error("ending Shot down Error", e.getMessage());
            e.printStackTrace();
            return new ResponsePointState("ending Shot down Error: " + e.getMessage());

        }
    }

    public void generate_wrong() {
        // checkOrReconnectPLC();
        if (!isPlcConnected()) {
            return;
        }
        try {
            plc.writeM(plc.getMPoint("generateWrong"), true);
            log.info("trigger generate wrong");

        } catch (Exception e) {
            plcConnected = false;
            log.error("trigger generate wrong Error", e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean reconnectPLC() {
        try {
            if (this.plc != null) {
                try {
                    this.plc.close();
                } catch (Exception ignored) {
                }
            }
            this.plc = new PLCDriver(plcIP, tcpPort);
            plcConnected = true;
            log.info("PLC connect success");
            System.out.println("PLC重連成功");
            return true;
        } catch (Exception e) {
            plcConnected = false;
            log.warn("cant connect to plc, error code: {}", e.getMessage());
            return false;
        }
    }

    public void setConnectStatus() {
        plcConnected = true;
    }

    public void setDisConnectStatus() {
        plcConnected = false;
    }
}