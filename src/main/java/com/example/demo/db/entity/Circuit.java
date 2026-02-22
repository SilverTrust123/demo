package com.example.demo.db.entity;

import jakarta.persistence.*;

// CREATE TABLE circuit (
//     device_id VARCHAR(255) NOT NULL,
//     voltage FLOAT,
//     current FLOAT,
//     power FLOAT,
//     energy FLOAT,
//     timestamp INT,
//     PRIMARY KEY (device_id, timestamp)
// );
@Entity
@Table(name = "circuit")
@IdClass(CircuitId.class)
public class Circuit {
    @Id
    @Column(name = "device_id")
    private String deviceId;
    @Id
    @Column(name = "timestamp")
    private int timestamp;
    private float voltage;
    private float current;
    private float power;
    private float energy;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public float getCurrent() {
        return current;
    }

    public void setCurrent(float current) {
        this.current = current;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }
}
