// package com.example.demo.SingleTest;

// import com.serotonin.modbus4j.ModbusFactory;
// import com.serotonin.modbus4j.ModbusMaster;
// import com.serotonin.modbus4j.ip.IpParameters;
// import com.serotonin.modbus4j.msg.WriteCoilRequest;
// import com.serotonin.modbus4j.msg.WriteRegisterRequest;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication
// public class ModbusTestApplication implements CommandLineRunner {

// public static void main(String[] args) {
// SpringApplication.run(ModbusTestApplication.class, args);
// }

// @Override
// public void run(String... args) throws Exception {
// System.out.println("====== 開始測試 PLC M點開關 ======");

// // 1. 設定 PLC 連線參數 (192.168.10.150:502)
// IpParameters params = new IpParameters();
// params.setHost("192.168.3.250");
// params.setPort(502);

// ModbusMaster master = new ModbusFactory().createTcpMaster(params, true);
// master.setTimeout(2000);

// try {
// master.init();
// System.out.println("1. 連線成功！");

// int slaveId = 1; // PLC 站號
// int mPoint = 0; // 假設要控制 M0 (Offset 從 0 開始)

// // 2. 打開 M 點 (Set Coil to TRUE)
// System.out.println("2. 正在打開 M" + mPoint + "...");
// WriteCoilRequest writeOn = new WriteCoilRequest(slaveId, mPoint, true);
// master.send(writeOn);
// System.out.println(">> M" + mPoint + " 已設為 ON");

// // 3. 等待 1 秒 (讓你觀察 PLC 燈號或狀態)
// Thread.sleep(10000);

// // 4. 關閉 M 點 (Set Coil to FALSE)
// System.out.println("3. 正在關閉 M" + mPoint + "...");
// WriteCoilRequest writeOff = new WriteCoilRequest(slaveId, mPoint, false);
// master.send(writeOff);
// System.out.println(">> M" + mPoint + " 已設為 OFF");

// } catch (Exception e) {
// System.err.println("連線失敗或 PLC 無回應: " + e.getMessage());
// } finally {
// master.destroy();
// System.out.println("====== 測試結束，已關閉連線 ======");
// }
// }
// }