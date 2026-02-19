package com.example.demo.DTO.requestDTO;

public class RequestAirQualityDTO {
    private String deviceId;
    private float airPollution;
    private int timestamp;

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

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
