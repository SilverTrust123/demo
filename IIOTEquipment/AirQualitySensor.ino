#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>
#include <ArduinoJson.h>

// --- 設定區 ---
const char* ssid = "yang";
const char* password = "20050116";
const char* serverUrl = "http://192.168.3.253/data";

const int mq135Pin = A0; // ESP8266 的類比輸入腳位只有A0
unsigned long lastTime = 0;
unsigned long timerDelay = 2000; // 發送頻率(2)

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

  Serial.println("MQ135 預熱中...");
}

void loop() {
  // 每隔一段時間執行一次發送
  if ((millis() - lastTime) > timerDelay) {
    if (WiFi.status() == WL_CONNECTED) {
      
      // 1. 讀取 MQ135 數值
      int sensorValue = analogRead(mq135Pin);
      float voltage = sensorValue * (3.3 / 1023.0); // ESP8266 ADC 電壓上限通常為 3.3V
      
      Serial.print("目前數值: ");
      Serial.print(sensorValue);
      Serial.print(" | 電壓: ");
      Serial.println(voltage);

      // 2. 建立 JSON 文件
      StaticJsonDocument<200> doc;
      doc["device_id"] = "ESP8266_01";
      doc["sensor"] = "MQ135";
      doc["value"] = sensorValue;
      doc["voltage"] = voltage;

      String jsonOutput;
      serializeJson(doc, jsonOutput);

      // 3. 發送 HTTP POST
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