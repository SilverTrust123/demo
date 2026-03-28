package com.example.demo.SingleTest;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.ip.IpParameters;

public class ReadD10Test {

    public static void main(String[] args) {

        String plcIp = "192.168.3.250";
        int port = 502;
        int slaveId = 1;

        ModbusMaster master = null;

        try {
            System.out.println("====== 讀取 D10 ======");

            IpParameters params = new IpParameters();
            params.setHost(plcIp);
            params.setPort(port);

            master = new ModbusFactory().createTcpMaster(params, true);
            master.init();

            System.out.println("✅ PLC 連線成功");

            // 👉 用 DataType（舊版也支援）
            BatchRead<Integer> batch = new BatchRead<>();
            batch.addLocator(0, BaseLocator.holdingRegister(slaveId, 20, DataType.TWO_BYTE_INT_SIGNED));

            BatchResults<Integer> results = master.send(batch);

            // int value = results.getIntValue(null);

            System.out.println("📊 D10 = " + results);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (master != null)
                master.destroy();
            System.out.println("====== 結束 ======");
        }
    }
}