package com.example.demo.SingleTest;

// import java.net.Socket;

// public class TestTCP {
//     public static void main(String[] args) {
//         try (Socket s = new Socket("192.168.3.250", 502)) {
//             System.out.println("connect success");
//         } catch (Exception e) {
//             System.out.println("connect failed: " + e.getMessage());
//         }
//     }
// }

// import com.serotonin.modbus4j.ModbusFactory;
// import com.serotonin.modbus4j.ModbusMaster;
// import com.serotonin.modbus4j.ip.IpParameters;
// import com.serotonin.modbus4j.msg.WriteCoilRequest;
// import com.serotonin.modbus4j.msg.WriteCoilResponse;

// public class M0Test {

//     public static void main(String[] args) {

//         String plcIp = "192.168.3.250";
//         int port = 502;
//         int slaveId = 1;

//         // ⚠️ 這裡很關鍵（等等會解釋）
//         int m0Address = 1;

//         ModbusMaster master = null;

//         try {
//             System.out.println("====== 開始測試 M0 ======");

//             // 建立連線
//             IpParameters params = new IpParameters();
//             params.setHost(plcIp);
//             params.setPort(port);

//             master = new ModbusFactory().createTcpMaster(params, true);
//             master.setTimeout(2000);
//             master.init();

//             System.out.println("✅ PLC 連線成功");

//             // 🔵 打開 M0
//             System.out.println("👉 打開 M0...");
//             WriteCoilRequest onReq = new WriteCoilRequest(slaveId, m0Address, true);
//             WriteCoilResponse onRes = (WriteCoilResponse) master.send(onReq);

//             if (onRes.isException()) {
//                 System.out.println("❌ 開啟失敗: " + onRes.getExceptionCode());
//             } else {
//                 System.out.println("✅ M0 ON 成功");
//             }

//             Thread.sleep(3000);

//             // 🔴 關閉 M0
//             System.out.println("👉 關閉 M0...");
//             WriteCoilRequest offReq = new WriteCoilRequest(slaveId, m0Address, false);
//             WriteCoilResponse offRes = (WriteCoilResponse) master.send(offReq);

//             if (offRes.isException()) {
//                 System.out.println("❌ 關閉失敗: " + offRes.getExceptionCode());
//             } else {
//                 System.out.println("✅ M0 OFF 成功");
//             }

//         } catch (Exception e) {
//             System.out.println("❌ 發生錯誤: " + e.getMessage());
//             e.printStackTrace();
//         } finally {
//             if (master != null) {
//                 master.destroy();
//             }
//             System.out.println("====== 測試結束 ======");
//         }
//     }
// }

// import com.serotonin.modbus4j.ModbusFactory;
// import com.serotonin.modbus4j.ModbusMaster;
// import com.serotonin.modbus4j.ip.IpParameters;
// import com.serotonin.modbus4j.msg.WriteCoilRequest;
// import com.serotonin.modbus4j.msg.WriteCoilResponse;

// import io.github.cdimascio.dotenv.Dotenv;

// public class M0Test {

// public static void M0Test(boolean on) throws Exception {
// String plcIp = "192.168.3.250";
// int port = 502;

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

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.WriteCoilRequest;
import com.serotonin.modbus4j.msg.WriteCoilResponse;

public class M0Test {

    public static void writeM21(boolean on) throws Exception {
        String plcIp = "192.168.3.250";
        int port = 502;

        // 設定 PLC IP 參數
        IpParameters ipParameters = new IpParameters();
        ipParameters.setHost(plcIp);
        ipParameters.setPort(port);

        // 建立 ModbusMaster
        ModbusFactory factory = new ModbusFactory();
        // 第二個參數 keepAlive 設為 true 增加連線穩定度
        ModbusMaster master = factory.createTcpMaster(ipParameters, true);

        try {
            master.init(); // 初始化連線

            int unitId = 1;

            // --- 重點校正區 ---
            // 如果 PLC 的 M21 對應 Modbus 地址是 21，就填 21
            // 如果沒反應，請嘗試改為 20 (0-based 索引)
            int coilAddress = 8193;
            // 8192 測試出來是 M0，8193 是 M1，依此類推 (M0~M9 分別是 8192~8201)

            // 寫入線圈 (Function Code 05)
            for (int i = 0; i < 15; i++) {
                WriteCoilRequest request = new WriteCoilRequest(unitId, coilAddress, on);
                master.send(request);
                // WriteCoilResponse response = (WriteCoilResponse) master.send(request);
                System.out.println("成功發送指令！ M" + coilAddress + " 狀態 -> " + (on ? "ON" : "OFF"));
                i++;
                coilAddress++;
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            System.err.println("連線發生異常: " + e.getMessage());
        } finally {
            master.destroy(); // 確保無論成功失敗都會釋放資源
        }

    }

    public static void main(String[] args) throws Exception {
        // 執行測試
        writeM21(true); // 打開
        Thread.sleep(1000);
        writeM21(false); // 關閉

    }
}