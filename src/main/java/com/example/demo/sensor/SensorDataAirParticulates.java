package com.example.demo.sensor;

public class SensorDataAirParticulates {
    private String deviceId;
    private float pm2_5;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public float getPm2_5() {
        return pm2_5;
    }

    public void setPm2_5(float pm2_5) {
        this.pm2_5 = pm2_5;
    }

    @Override
    public String toString() {
        return "PM2.5: " + pm2_5;
    }
}
