package com.example.demo.plc;

import java.util.HashMap;
import java.util.Map;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.*;
// import com.serotonin.modbus4j.serial.*;
// import com.serotonin.modbus4j.serial.rtu.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PLCDriver {
    private ModbusMaster master;

    private final Map<String, MPoint> MPointMap = new PointList().getMPointMap();
    private final Map<String, DPoint> DPointMap = new PointList().getDPointMap();

    private static final Logger log = LoggerFactory.getLogger(PLCDriver.class);

    public PLCDriver(String ip, int port) throws Exception {
        // 先預留RTU街口 直接搬到PC上面實驗 當備案吧
        // SerialParameters params = new SerialParameters();
        // params.setCommPortId("COM3"); // Windows 用 COMx
        // params.setBaudRate(9600);
        // params.setDataBits(8);
        // params.setStopBits(1);
        // params.setParity(0); // 0=None, 1=Odd, 2=Even
        IpParameters params = new IpParameters();
        params.setHost(ip);
        params.setPort(port);
        this.master = new ModbusFactory().createTcpMaster(params, true);
        this.master.init();
        log.info("check list for M point {} ", MPointMap);
        log.info("check list for D point {} ", DPointMap);
    }

    public boolean MdeviceIsEmpty(String param) {
        return !hasMPoint(param);
    }

    public boolean DdeviceIsEmpty(String param) {
        return !hasDPoint(param);
    }

    public MPoint getMPoint(String name) {
        return MPointMap.get(name);
    }

    public DPoint getDPoint(String name) {
        return DPointMap.get(name);
    }

    public boolean hasMPoint(String name) {
        return MPointMap.containsKey(name);
    }

    public boolean hasDPoint(String name) {
        return DPointMap.containsKey(name);
    }

    public void writeM(MPoint point, boolean value) throws Exception {
        master.send(new WriteCoilRequest(1, point.getAddress(), value));
    }

    public boolean readM(MPoint point) throws Exception {
        ReadCoilsRequest request = new ReadCoilsRequest(1, point.getAddress(), 1);
        ReadCoilsResponse response = (ReadCoilsResponse) master.send(request);
        return response.getBooleanData()[0];
    }

    public void writeD(DPoint point, int value) throws Exception {
        master.send(new WriteRegisterRequest(1, point.getAddress(), value));
    }

    public int readD(DPoint point) throws Exception {
        ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(1, point.getAddress(), 1);
        ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) master.send(request);
        return response.getShortData()[0];
    }

    public void close() {
        if (master != null)
            master.destroy();
    }

    public HashMap<String, Integer> getAllDPoints() throws Exception {
        HashMap<String, Integer> ans = new HashMap<>();
        for (String curr : DPointMap.keySet()) {
            ans.put(curr, Integer.valueOf(readD(getDPoint(curr))));
        }
        return ans;
    }

    public HashMap<String, Boolean> getAllMPoints() throws Exception {
        HashMap<String, Boolean> ans = new HashMap<>();
        for (String curr : MPointMap.keySet()) {
            ans.put(curr, readM(getMPoint(curr)));
        }
        return ans;
    }

    public int getCountMetal() throws Exception {
        return readD(DPointMap.get("COUNT_METAL"));
    }

    public int getCountNonMetal() throws Exception {
        return readD(DPointMap.get("COUNT_NON_METAL"));
    }

    public Map<String, MPoint> getMPointMap() {
        return MPointMap;
    }

    public Map<String, DPoint> getDPointMap() {
        return DPointMap;
    }
}