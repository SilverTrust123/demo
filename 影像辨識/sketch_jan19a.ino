#include <ESP8266WiFi.h>
#include <ArduinoJson.h>

const int measurePin = A0;    // 連接到感測器的第5腳
const int ledPower = D2;      // 連接到感測器的第3腳

// --- 感測器參數 ---
const int samplingTime = 280;
const int deltaTime = 40;
const int sleepTime = 9680;

float voMeasured = 0;
float calcVoltage = 0;
float dustDensity = 0;

// --- 設備資訊 (對應你的 deviceId) ---
const char* deviceId = "ESP8266_3";

void setup() {
  Serial.begin(9600);
  pinMode(ledPower, OUTPUT);
  
  Serial.println("\n感測器啟動中...");
}

void loop() {
  // 1. 讀取感測器數值
  digitalWrite(ledPower, LOW); // 開啟內部 LED (低電位觸發)
  delayMicroseconds(samplingTime);

  voMeasured = analogRead(measurePin); // 讀取類比值

  delayMicroseconds(deltaTime);
  digitalWrite(ledPower, HIGH); // 關閉內部 LED
  delayMicroseconds(sleepTime);

  // 2. 電壓轉換與 PM2.5 計算
  // 假設使用分壓電阻，若沒用請調整換算係數
  calcVoltage = voMeasured * (3.3 / 1024.0); 
  
  // GP2Y1010AU0F 線性公式: 
  // Dust Density = 0.17 * voltage - 0.1
  dustDensity = 0.17 * calcVoltage - 0.1;

  if (dustDensity < 0) dustDensity = 0; // 修正負值

  // 3. 封裝成 JSON
  StaticJsonDocument<128> doc;
  doc["deviceId"] = deviceId;
  doc["pm2_5"] = (int)(dustDensity * 1000); // 轉換為整數，單位通常為 ug/m3

  // 4. 輸出結果
  serializeJson(doc, Serial);
  Serial.println();

  delay(2000); // 每兩秒測量一次
}