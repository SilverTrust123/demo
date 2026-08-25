package com.example.demo.db.entity;

import java.io.Serializable;
import java.util.Objects;

public class SensorBorderId implements Serializable {
    private String sensor_group;
    private int timestamp;

    public SensorBorderId() {
    }

    public SensorBorderId(String sensor_group, int timestamp) {
        this.sensor_group = sensor_group;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SensorBorderId that = (SensorBorderId) o;
        return timestamp == that.timestamp && Objects.equals(sensor_group, that.sensor_group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensor_group, timestamp);
    }
}