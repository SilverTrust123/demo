package com.example.demo.db.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "air_quality")
@IdClass(AirQualityId.class)
public class AirQuality {
    // CREATE TABLE air_quality (
    // device_id VARCHAR(255) NOT NULL,
    // air_pollution FLOAT,
    // timestamp INT,
    // PRIMARY KEY (device_id, timestamp)
    // );
    @Id
    @Column(name = "device_id")
    private String deviceId;
    @Id
    @Column(name = "timestamp")
    private int timestamp;

    private float airPollution;

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

    public float getAirPollution() {
        return airPollution;
    }

    public void setAirPollution(float airPollution) {
        this.airPollution = airPollution;
    }
}
