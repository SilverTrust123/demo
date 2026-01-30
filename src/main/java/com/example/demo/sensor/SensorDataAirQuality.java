package com.example.demo.sensor;

public class SensorDataAirQuality {
    private String deviceId;
    private int airPollution;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getAirPollution() {
        return airPollution;
    }

    public void setAirPollution(int airPollution) {
        this.airPollution = airPollution;
    }

    @Override
    public String toString() {
        return "Air Pollution: " + airPollution;
    }
}
