package com.example.demo.DTO.responseDTO;

public record ResponseAirParticulatesDTO(String deviceId, float pm2_5, int timestamp) {
}
// private String deviceId;
// private float pm2_5;
// private int timestamp;

// public ResponseAirParticulatesDTO(String deviceId, float pm2_5, int
// timestamp) {
// this.deviceId = deviceId;
// this.pm2_5 = pm2_5;
// this.timestamp = timestamp;
// }

// public int getTimestamp() {
// return timestamp;
// }

// public void setTimestamp(int timestamp) {
// this.timestamp = timestamp;
// }

// public String getDeviceId() {
// return deviceId;
// }

// public void setDeviceId(String deviceId) {
// this.deviceId = deviceId;
// }

// public float getPm2_5() {
// return pm2_5;
// }

// public void setPm2_5(float pm2_5) {
// this.pm2_5 = pm2_5;
// }

// @Override
// public String toString() {
// return "PM2.5: " + pm2_5;
// }
// }
