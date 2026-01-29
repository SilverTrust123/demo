#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <DHT11.h>

#define DHTPIN D4
DHT11 dht11(DHTPIN);
const char* ssid = "Xa10yah28";
const char* password = "yaya123123";
const char* serverUrl = "http://10.246.169.167:9090/circultData";

void setup() {
  Serial.begin(9600);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
}

void loop() {
  int circult = 0;
  Serial.print(circult);

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

















// const char* ssid = "Xa10yah28";
// const char* password = "yaya123123";
// const char* serverUrl = "https://Ngrok網址/data";


// const char* ssid = "157-4F";
// const char* password = "00000000";
// const char* serverUrl = "http://192.168.1.104:9090/circultData";