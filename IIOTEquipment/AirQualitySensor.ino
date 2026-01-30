#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>
#include <ArduinoJson.h>
#include <math.h>

// --- WiFi & Server 設定 ---
const char* ssid = "yang";
const char* password = "20050116";
const char* serverUrl = "http://192.168.3.253/AirQualityData";

// --- MQ-135 設定 ---
const int airQualityPin = A0;    // MQ-135 接在 A0
const float RL = 20.0;            // Load resistor kΩ
float R0 = 10.0;                  // 預設基準阻值（需先校正乾淨空氣得到）

unsigned long lastTime = 0;
unsigned long timerDelay = 1000;  // 2 秒發送一次

void setup() {
  Serial.begin(115200);

  // 初始化 WiFi
  WiFi.begin(ssid, password);
  Serial.print("正在連線至 WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi 已連線");
  Serial.print("IP 地址: ");
  Serial.println(WiFi.localIP());

  Serial.println("MQ135 預熱中... 請保持乾淨空氣進行校正");

  // 這裡簡單校正 R0（可手動測一次乾淨空氣平均值再填入 R0）
  // R0 = Rs in clean air
  // 建議校正 1 分鐘取平均
}

void loop() {
  if ((millis() - lastTime) > timerDelay) {
    if (WiFi.status() == WL_CONNECTED) {

      // 1. 讀取 MQ-135 電壓
      int sensorValue = analogRead(airQualityPin);
      float voltage = sensorValue * (3.3 / 1023.0);  // ADC → 電壓

      // 2. 計算感測器阻值 Rs
      float rs = ((3.3 - voltage) / voltage) * RL; // kΩ

      // 3. 計算濃度 ppm (以 CO2 曲線為例)
      // ppm = 116.6020682 * (Rs/R0)^(-2.769034857)
      float rs_r0 = rs / R0;
      float ppm = 116.6020682 * pow(rs_r0, -2.769034857);

      Serial.print("SensorValue: ");
      Serial.print(sensorValue);
      Serial.print(" | Voltage: ");
      Serial.print(voltage);
      Serial.print("V | Rs: ");
      Serial.print(rs);
      Serial.print("kΩ | PPM: ");
      Serial.println(ppm);

      // 4. 建立 JSON 文件對應 SensorDataAirQuality
      StaticJsonDocument<200> doc;
      doc["deviceId"] = "ESP8266_3";    // 裝置 ID
      doc["airPollution"] = (int)ppm;    // 整數 ppm

      String jsonOutput;
      serializeJson(doc, jsonOutput);

      // 5. 發送 HTTP POST
      WiFiClient client;
      HTTPClient http;

      http.begin(client, serverUrl);
      http.addHeader("Content-Type", "application/json");

      int httpResponseCode = http.POST(jsonOutput);

      if (httpResponseCode > 0) {
        Serial.print("發送成功，回應碼: ");
        Serial.println(httpResponseCode);
        String payload = http.getString();
        Serial.println("伺服器回應: " + payload);
      } else {
        Serial.print("發送失敗，錯誤代碼: ");
        Serial.println(httpResponseCode);
      }

      http.end();
    } else {
      Serial.println("WiFi 連線中斷");
    }

    lastTime = millis();
  }
}
