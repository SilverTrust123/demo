package com.example.demo.SingleTest;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.WriteCoilRequest;
import com.serotonin.modbus4j.msg.WriteCoilResponse;

public class WriteM0Test {

    public static void main(String[] args) {

        String plcIp = "192.168.3.250"; // PLC IP
        int port = 502; // Modbus TCP port
        int slaveId = 1; // PLC 站號
        int mPoint = 10; // M0

        ModbusMaster master = null;

        try {
            System.out.println("====== 打開 M0 ======");

            // 1️⃣ 設定 PLC TCP 連線
            IpParameters params = new IpParameters();
            params.setHost(plcIp);
            params.setPort(port);

            master = new ModbusFactory().createTcpMaster(params, true);
            master.setTimeout(2000);
            master.init();
            System.out.println("✅ PLC 連線成功");

            // 2️⃣ 寫入 M0 = ON
            System.out.println("正在打開 M0...");
            WriteCoilRequest request = new WriteCoilRequest(slaveId, mPoint, true);
            WriteCoilResponse response = (WriteCoilResponse) master.send(request);

            if (response.isException()) {
                System.out.println("❌ M0 開啟失敗，錯誤碼: " + response.getExceptionCode());
            } else {
                System.out.println("✅ M0 已經開啟");
            }

            // 3️⃣ 等 5 秒後關閉 (可選)
            Thread.sleep(5000);

            System.out.println("正在關閉 M0...");
            WriteCoilRequest requestOff = new WriteCoilRequest(slaveId, mPoint, false);
            WriteCoilResponse responseOff = (WriteCoilResponse) master.send(requestOff);

            if (responseOff.isException()) {
                System.out.println("❌ M0 關閉失敗，錯誤碼: " + responseOff.getExceptionCode());
            } else {
                System.out.println("✅ M0 已經關閉");
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