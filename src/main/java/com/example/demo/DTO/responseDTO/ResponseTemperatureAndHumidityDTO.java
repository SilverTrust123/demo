package com.example.demo.DTO.responseDTO;

public record ResponseTemperatureAndHumidityDTO(String deviceId, float temperature, float humidity, int timestamp) {
}
// private String deviceId;
// private float temperature;
// private float humidity;
// private int timestamp;

// public ResponseTemperatureAndHumidityDTO(String deviceId, float temperature,
// float humidity, int timestamp) {
// this.deviceId = deviceId;
// this.temperature = temperature;
// this.humidity = humidity;
// this.timestamp = timestamp;
// }

// public int getTimestamp() {
// return timestamp;
// }

// public void setTimestamp(int timestamp) {
// this.timestamp = timestamp;
// }

// public String getDeviceId() {
// return deviceId;
// }

// public void setDeviceId(String deviceId) {
// this.deviceId = deviceId;
// }

// public float getTemperature() {
// return temperature;
// }

// public void setTemperature(float temperature) {
// this.temperature = temperature;
// }

// public float getHumidity() {
// return humidity;
// }

// public void setHumidity(float humidity) {
// this.humidity = humidity;
// }

// @Override
// public String toString() {
// return "Temperature: " + temperature + "°C, Humidity: " + humidity + "%";
// }
// }
