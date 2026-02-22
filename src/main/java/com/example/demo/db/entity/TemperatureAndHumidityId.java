package com.example.demo.db.entity;

import java.io.Serializable;
import java.util.Objects;

// 這只是用來定義「鑰匙」組合的
public class TemperatureAndHumidityId implements Serializable {
    private String deviceId;
    private int timestamp;

    public TemperatureAndHumidityId() {
    }

    public TemperatureAndHumidityId(String deviceId, int timestamp) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
    }

    // 複合主鍵必須實作 equals 和 hashCode，JPA 才能比對鑰匙
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TemperatureAndHumidityId that = (TemperatureAndHumidityId) o;
        return timestamp == that.timestamp && Objects.equals(deviceId, that.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, timestamp);
    }
}