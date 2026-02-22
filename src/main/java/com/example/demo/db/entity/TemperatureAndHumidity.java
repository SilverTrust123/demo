package com.example.demo.db.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "temperature_and_humidity")
@IdClass(TemperatureAndHumidityId.class) // 告訴它鑰匙長在那邊
public class TemperatureAndHumidity {

    @Id
    @Column(name = "device_id")
    private String deviceId;

    @Id
    @Column(name = "timestamp")
    private int timestamp;

    private Float temperature;
    private Float humidity;

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

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }
}