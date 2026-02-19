package com.example.demo.DTO.responseDTO;

public class ResponseAirQualityDTO {
    private String deviceId;
    private float airPollution;
    private int timestamp;

    public ResponseAirQualityDTO(String deviceId, float airPollution, int timestamp) {
        this.deviceId = deviceId;
        this.airPollution = airPollution;
        this.timestamp = timestamp;
    }

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
