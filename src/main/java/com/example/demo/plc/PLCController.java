package com.example.demo.plc;

import java.util.HashMap;
import java.util.Map;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;
// import com.serotonin.modbus4j.serial.*;
// import com.serotonin.modbus4j.serial.rtu.*;
import com.serotonin.modbus4j.msg.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PLCController {
    private ModbusMaster master;

    private final MPoint RTESTART = new MPoint(90, "重來");
    private final MPoint RESET_ALL_TIMERELAY = new MPoint(91, "重置所有計時繼電器");

    private final DPoint STATE = new DPoint(200, "狀態");
    private final DPoint COUNT_METAL = new DPoint(1000, "金屬件數");
    private final DPoint COUNT_NON_METAL = new DPoint(1001, "非金屬件數");
    private final DPoint T14 = new DPoint(14, "東西來等多久");
    private final DPoint T0 = new DPoint(0, "等判斷");
    private final DPoint T30 = new DPoint(30, "龍門等多久開始向下");
    private final DPoint T3 = new DPoint(3, "龍門確保向下等多久");
    private final DPoint T4 = new DPoint(4, "龍門夾到等多久");
    private final DPoint T31 = new DPoint(31, "龍門到左邊停多久");
    private final DPoint T5 = new DPoint(5, "龍們向下停多久");
    private final DPoint T6 = new DPoint(6, "龍門放開停多久");
    private final DPoint T15 = new DPoint(15, "龍門向上停多久");
    private final DPoint T7 = new DPoint(7, "輸送帶2上面的感測器感測沒有東西停多久才繼續");
    private final DPoint T8 = new DPoint(8, "選轉機械臂向下停多久");
    private final DPoint T9 = new DPoint(9, "選轉機械臂真空泵抽真空之後等多久");
    private final DPoint T32 = new DPoint(32, "旋轉機械臂迴轉過去停多久");
    private final DPoint T10 = new DPoint(10, "旋轉機械臂向下停多久");
    private final DPoint T11 = new DPoint(11, "選轉機械臂放真空等多久");
    private final DPoint T40 = new DPoint(40, "滑台機械臂向下停多久");
    private final DPoint T12 = new DPoint(12, "滑台機械臂夾到之後等多久");
    private final DPoint T13 = new DPoint(13, "滑台機械臂放開之後等多久");

    private final Map<String, MPoint> MPointMap = new HashMap<>();
    private final Map<String, DPoint> DPointMap = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(PLCController.class);

    // 先預留RTU街口 直接搬到PC上面實驗 當備案吧
    // SerialParameters params = new SerialParameters();
    // params.setCommPortId("COM3"); // Windows 用 COMx, Linux 用 /dev/ttyUSBx
    // params.setBaudRate(9600);
    // params.setDataBits(8);
    // params.setStopBits(1);
    // params.setParity(0); // 0=None, 1=Odd, 2=Even

    public PLCController(String ip, int port) throws Exception {
        IpParameters params = new IpParameters();
        params.setHost(ip);
        params.setPort(port);
        this.master = new ModbusFactory().createTcpMaster(params, true);
        this.master.init();
        // 間加M點
        MPointMap.put("RTESTART", RTESTART);
        MPointMap.put("RESET_ALL_TIMERELAY", RESET_ALL_TIMERELAY);
        // 添加D點
        DPointMap.put("STATE", STATE);
        DPointMap.put("COUNT_METAL", COUNT_METAL);
        DPointMap.put("COUNT_NON_METAL", COUNT_NON_METAL);
        DPointMap.put("T14", T14);
        DPointMap.put("T0", T0);
        DPointMap.put("T30", T30);
        DPointMap.put("T3", T3);
        DPointMap.put("T4", T4);
        DPointMap.put("T31", T31);
        DPointMap.put("T5", T5);
        DPointMap.put("T6", T6);
        DPointMap.put("T15", T15);
        DPointMap.put("T7", T7);
        DPointMap.put("T8", T8);
        DPointMap.put("T9", T9);
        DPointMap.put("T32", T32);
        DPointMap.put("T10", T10);
        DPointMap.put("T11", T11);
        DPointMap.put("T40", T40);
        DPointMap.put("T12", T12);
        DPointMap.put("T13", T13);
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

}