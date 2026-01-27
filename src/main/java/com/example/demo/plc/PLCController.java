package com.example.demo.plc;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.*;

public class PLCController {
    private ModbusMaster master;

    public final MPoint PUMP_STATUS = new MPoint(21, "狀態");
    public final DPoint CURRENT_TEMP = new DPoint(100, "當前溫度");
    private final Map<String, MPoint> MPointMap = new HashMap<>();
    private final Map<String, DPoint> DPointMap = new HashMap<>();

    public PLCController(String ip, int port) throws Exception {
        IpParameters params = new IpParameters();
        params.setHost(ip);
        params.setPort(port);
        this.master = new ModbusFactory().createTcpMaster(params, true);
        this.master.init();
        // 間加M點
        MPointMap.put("PUMP_STATUS", PUMP_STATUS);
        // 添加D點
        DPointMap.put("CURRENT_TEMP", CURRENT_TEMP);
    }

    public boolean MdeviceIsEmpty(String param) {
        return !hasMPoint(param);
    }

    public boolean DdeviceIsEmpty(String param) {
        return !hasDPoint(param);
    }

    // 查詢設備物件(備用)
    public MPoint getMPoint(String name) {
        return MPointMap.get(name);
    }

    // 查詢是否存在
    public boolean hasMPoint(String name) {
        return MPointMap.containsKey(name);
    }

    // M點
    public void writeM(MPoint point, boolean value) throws Exception {
        master.send(new WriteCoilRequest(1, point.getAddress(), value));
    }

    public boolean readM(MPoint point) throws Exception {
        ReadCoilsRequest request = new ReadCoilsRequest(1, point.getAddress(), 1);
        ReadCoilsResponse response = (ReadCoilsResponse) master.send(request);
        return response.getBooleanData()[0];
    }

    // D點
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
}

// // package com.example.demo.plc;

// import com.serotonin.modbus4j.ModbusFactory;
// import com.serotonin.modbus4j.ModbusMaster;
// import com.serotonin.modbus4j.ip.IpParameters;
// import com.serotonin.modbus4j.msg.WriteCoilRequest;
// import com.serotonin.modbus4j.msg.WriteCoilResponse;

// public class PLCControler {

// private Dotenv dotenv = Dotenv.load();
// private String dbUser = dotenv.get("DB_USER");
// private String dbPassword = dotenv.get("DB_PASSWORD");

// public static void writeM21(boolean on) throws Exception {
// // PLC IP 與 Port
// // String plcIp = "192.168.3.250";
// // int port = 502;

// // 設定 PLC IP 參數
// IpParameters ipParameters = new IpParameters();
// ipParameters.setHost(plcIp);
// ipParameters.setPort(port);

// // 建立 ModbusMaster
// ModbusFactory factory = new ModbusFactory();
// ModbusMaster master = factory.createTcpMaster(ipParameters, true);
// master.init(); // 初始化連線

// int unitId = 1; // Modbus Unit ID
// int coilAddress = 1; // M21 的位址

// // 寫入線圈
// WriteCoilRequest request = new WriteCoilRequest(unitId, coilAddress, on);
// WriteCoilResponse response = (WriteCoilResponse) master.send(request);

// if (response.isException()) {
// System.out.println("寫入失敗，錯誤碼: " + response.getExceptionCode());
// } else {
// System.out.println("M21 已經 " + (on ? "ON" : "OFF"));
// }

// master.destroy(); // 關閉連線
// }

// // public static void main(String[] args) throws Exception {
// // writeM21(true); // 打開 M21
// // Thread.sleep(2000); // 等 2 秒
// // writeM21(false); // 關閉 M21
// // }
// }

// package com.example.demo.plc;

// public class OtherController {
// public void monitorSystem() {
// try {
// PLCController plc = new PLCController("192.168.3.250", 502);

// // 1. 讀取狀態
// boolean isRunning = plc.readM(plc.PUMP_STATUS);
// int temperature = plc.readD(plc.CURRENT_TEMP);

// System.out.println("當前水泵: " + (isRunning ? "運轉中" : "停止"));
// System.out.println("當前溫度: " + temperature + "度");

// // 2. 根據讀取到的數值做邏輯判斷
// if (temperature > 80 && isRunning) {
// System.out.println("警告：溫度過高，關閉水泵！");
// plc.writeM(plc.PUMP_STATUS, false);
// }

// plc.close();
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
// }