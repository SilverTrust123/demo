package com.example.demo;

public class SensorDataAirParticulates {
    private String deviceId;
    private int pm2_5;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getPm2_5() {
        return pm2_5;
    }

    public void setPm2_5(int pm2_5) {
        this.pm2_5 = pm2_5;
    }

    @Override
    public String toString() {
        return "PM2.5: " + pm2_5;
    }
}
