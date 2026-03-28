#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <DHT11.h>

#define DHTPIN D4
DHT11 dht11(DHTPIN);
const char* ssid = "hitchcook";
const char* password = "imsohandsome";
const char* serverUrl = "http://192.168.3.110:9090/temperatureAndHumidityData/";

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