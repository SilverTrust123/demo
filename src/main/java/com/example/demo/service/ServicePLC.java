package com.example.demo.service;

import org.slf4j.LoggerFactory;

import java.util.HashMap;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.example.demo.plc.PLCDriver;
import com.example.demo.DTO.responseDTO.PLCResponseDTO.*;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@Service
public class ServicePLC {
    private PLCDriver plc;
    Dotenv dotenv = Dotenv.load();
    private String plcIP = dotenv.get("PLC_IP");
    private int tcpPort = Integer.parseInt(dotenv.get("TCP_PORT"));
    private static final Logger log = LoggerFactory.getLogger(ServicePLC.class);
    private boolean plcConnected = false;

    @PostConstruct
    public void init() {
        try {
            this.plc = new PLCDriver(plcIP, tcpPort);
            plcConnected = true;
            log.info("PLC connect secces");
            System.out.println("PLC 連線成功");
        } catch (Exception e) {
            log.error("cant connect to plc, error code", e.getMessage());
            System.err.println("PLC 連線失敗 code: " + e.getMessage());
        }
    }

    public ResponsePointState MpointState(String param) {
        log.info("receieved read m point request {} ", param);
        try {
            if (param == null || param.isEmpty()) {
                log.warn("MPointState: param is null or empty");
                return new ResponsePointState("MPointState: param is null or empty");
            }

            if (plc.MdeviceIsEmpty(param)) {
                log.warn("MPointState: device not found {} ", param);
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
                return new ResponsePointState("NoDevice");
            }

            if (plc.DdeviceIsEmpty(param)) {
                log.warn("DPointData: device not found -> {}", param);
                return new ResponsePointState("NoDevice");
            }

            int val = plc.readD(plc.getDPoint(param));
            log.info("DPointData: {} = {}", param, val);
            return new ResponsePointState(String.valueOf(val));

        } catch (Exception e) {
            log.error("DPointData error param {} with error {}", param, e.getMessage());
            e.printStackTrace();
            return new ResponsePointState("Error");
        }
    }

    public ResponseAllDPointStateDTO AllDPointData() throws Exception {
        HashMap<String, Integer> curr = plc.getAllDPoints();
        log.info("received get all DPoint request");
        if (curr.isEmpty()) {
            log.warn("no DPoint data its empty");
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
            return new ResponseAllMPointStateDTO();
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
            return new ResponsePointState("PLC Disconnected: " + e.getMessage());
        }
    }

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

}
