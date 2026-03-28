package com.example.demo.SingleTest;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.WriteRegisterRequest;
import com.serotonin.modbus4j.msg.WriteRegisterResponse;

public class WriteD20Test {

    public static void main(String[] args) {

        String plcIp = "192.168.3.250"; // PLC IP
        int port = 502; // Modbus TCP port
        int slaveId = 1; // PLC 站號
        int dAddress = 20; // D20
        int kValue = 200; // K20

        ModbusMaster master = null;

        try {
            System.out.println("====== 寫入 D20 ======");

            // 1️⃣ 設定 PLC TCP 連線
            IpParameters params = new IpParameters();
            params.setHost(plcIp);
            params.setPort(port);

            master = new ModbusFactory().createTcpMaster(params, true);
            master.setTimeout(2000);
            master.init();
            System.out.println("✅ PLC 連線成功");

            // 2️⃣ 寫入 D20 = K20
            System.out.println("正在寫入 D20 = " + kValue);
            WriteRegisterRequest request = new WriteRegisterRequest(slaveId, dAddress, kValue);
            WriteRegisterResponse response = (WriteRegisterResponse) master.send(request);

            if (response.isException()) {
                System.out.println("❌ 寫入失敗，錯誤碼: " + response.getExceptionCode());
            } else {
                System.out.println("✅ D20 已寫入 " + kValue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (master != null)
                master.destroy();
            System.out.println("====== 結束 ======");
        }
    }
}