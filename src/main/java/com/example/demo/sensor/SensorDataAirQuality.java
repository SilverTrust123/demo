package com.example.demo.sensor;

public class SensorDataAirQuality {
    private String deviceId;
    private float airPollution;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public float getAirPollution() {
        return airPollution;
    }

    public void setAirPollution(float airPollution) {
        this.airPollution = airPollution;
    }

    @Override
    public String toString() {
        return "Air Pollution: " + airPollution;
    }
}
