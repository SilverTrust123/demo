package com.example.demo.db.entity;

import java.io.Serializable;
import java.util.Objects;

public class CircuitId implements Serializable {
    private String deviceId;
    private int timestamp;

    public CircuitId() {
    }

    public CircuitId(String deviceId, int timestamp) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CircuitId that = (CircuitId) o;
        return timestamp == that.timestamp && Objects.equals(deviceId, that.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, timestamp);
    }
}