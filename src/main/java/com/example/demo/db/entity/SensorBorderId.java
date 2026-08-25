package com.example.demo.db.entity;

import java.io.Serializable;
import java.util.Objects;

public class SensorBorderId implements Serializable {
    private String sensorGroup;
    private int timestamp;

    public SensorBorderId() {
    }

    public SensorBorderId(String sensorGroup, int timestamp) {
        this.sensorGroup = sensorGroup;
        this.timestamp = timestamp;
    }

    public String getSensorGroup() {
        return sensorGroup;
    }

    public void setSensorGroup(String sensorGroup) {
        this.sensorGroup = sensorGroup;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SensorBorderId that = (SensorBorderId) o;
        return timestamp == that.timestamp && Objects.equals(sensorGroup, that.sensorGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorGroup, timestamp);
    }
}