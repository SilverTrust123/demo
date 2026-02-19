package com.example.demo.DTO.requestDTO;

public class RequestCircuitDTO {
    private String deviceId;
    private float voltage;
    private float current;
    private float power;
    private float energy;
    private int timestamp;

    public RequestCircuitDTO() {
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    // deviceId
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    // voltage
    public float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    // current
    public float getCurrent() {
        return current;
    }

    public void setCurrent(float current) {
        this.current = current;
    }

    // power
    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    // energy
    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    @Override
    public String toString() {
        return "DeviceId: " + deviceId + ", Voltage: " + voltage + ", Current: " + current + ", Power: " + power
                + ", Energy: " + energy;
    }
}
