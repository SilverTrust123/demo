package com.example.demo.db.entity;

import jakarta.persistence.*;
// CREATE TABLE air_particulates (
//     device_id VARCHAR(255) NOT NULL,
//     pm2_5 FLOAT,
//     timestamp INT,
//     PRIMARY KEY (device_id, timestamp)
// );

@Entity
@Table(name = "air_particulates")
@IdClass(AirParticulatesId.class)
public class AirParticulates {
    @Id
    @Column(name = "device_id")
    private String deviceId;
    @Id
    @Column(name = "timestamp")
    private int timestamp;

    private float pm2_5;

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

    public float getPm2_5() {
        return pm2_5;
    }

    public void setPm2_5(float pm2_5) {
        this.pm2_5 = pm2_5;
    }
}
