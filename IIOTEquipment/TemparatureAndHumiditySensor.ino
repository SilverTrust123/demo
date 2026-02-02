#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <DHT11.h>

#define DHTPIN D4
DHT11 dht11(DHTPIN);
const char* ssid = "157-4F";
const char* password = "00000000";
const char* serverUrl = "http://192.168.1.104:9090/TemparatureAndHumidityData";

void setup() {
  Serial.begin(9600);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
}

void loop() {
  int temperature = 0;
  int humidity = 0;
  Serial.print(temperature);
  Serial.print(humidity);

  int result = dht11.readTemperatureHumidity(temperature, humidity);

  if (result != 0) {
    Serial.println(DHT11::getErrorString(result));
    delay(2000);
    return;
  }

  Serial.printf("Temperature: %d °C, Humidity: %d %%\n", temperature, humidity);

  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    WiFiClient client;

    http.begin(client, serverUrl);
    http.addHeader("Content-Type", "application/json");

    String json = "{\"deviceId\":\"8266_2\",\"temperature\":"
                  + String(temperature)
                  + ",\"humidity\":"
                  + String(humidity)
                  + "}";

    int httpResponseCode = http.POST(json);
    Serial.printf("HTTP Response code: %d\n", httpResponseCode);

    http.end();
  }

  delay(1000);
}








// #include <ESP8266WiFi.h>
// #include <ESP8266HTTPClient.h>
// #include <WiFiClient.h>
// #include <DHT11.h>

// #define DHTPIN D4
// DHT11 dht11(DHTPIN);

// // --- Wi-Fi 設定 ---
// const char* ssid = "yang";
// const char* password = "20050116";
// const char* serverUrl = "http://192.168.3.253:9090/TemparatureAndHumidityData";

// // --- 強制指定 IP 網段 ---
// IPAddress local_IP(192, 168, 3, 254); // 把 ESP8266 定在 192.168.3 網段
// IPAddress gateway(0, 0, 0, 0);    // 你的熱點網關
// IPAddress subnet(255, 255, 255, 0);

// void setup() {
//   Serial.begin(9600);
//   delay(1000);

//   // 在連線前強制設定靜態 IP
//   if (!WiFi.config(local_IP, gateway, subnet)) {
//     Serial.println("Static IP Configuration Failed!");
//   }

//   WiFi.begin(ssid, password);
//   Serial.print("Connecting to WiFi");
//   while (WiFi.status() != WL_CONNECTED) {
//     delay(500);
//     Serial.print(".");
//   }
//   Serial.println("\nConnected!");
//   Serial.print("IP Address: ");
//   Serial.println(WiFi.localIP()); // 這裡必須顯示 192.168.3.117
// }

// void loop() {
//   int temperature = 0;
//   int humidity = 0;

//   // 讀取感測器
//   int result = dht11.readTemperatureHumidity(temperature, humidity);

//   if (result == 0) {
//     Serial.printf("Temp: %d °C, Hum: %d %%\n", temperature, humidity);

//     if (WiFi.status() == WL_CONNECTED) {
//       WiFiClient client;
//       HTTPClient http;

//       http.begin(client, serverUrl);
//       http.addHeader("Content-Type", "application/json");

//       // 建立 JSON 字串
//       String json = "{\"deviceId\":\"8266_1\",\"temperature\":" + String(temperature) + 
//                     ",\"humidity\":" + String(humidity) + "}";

//       int httpResponseCode = http.POST(json);
//       Serial.printf("HTTP Response code: %d\n", httpResponseCode);
      
//       if (httpResponseCode < 0) {
//         Serial.printf("Error: %s\n", http.errorToString(httpResponseCode).c_str());
//       }

//       http.end();
//     }
//   } else {
//     Serial.println(DHT11::getErrorString(result));
//   }

//   delay(5000); // 每 5 秒傳送一次
// }