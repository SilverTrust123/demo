package com.example.demo.sensor;

public class SensorDataTemperatureAndHumidity {
    private String deviceId;
    private float temperature;
    private float humidity;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "Temperature: " + temperature + "°C, Humidity: " + humidity + "%";
    }
}
