package com.example.demo.DTO.responseDTO;

import com.example.demo.DTO.requestDTO.DetectedObjectDTO;
import java.util.List;

public record ResponseCam(String deviceId, boolean danger, int personCount, List<List<Integer>> dangerZone,
        List<DetectedObjectDTO> objects, int timestamp) {
}

// private String deviceId;
// private boolean danger; // 是否危險
// private int personCount; // 人數
// private List<List<Integer>> dangerZone; // 危險區座標
// private List<DetectedObjectDTO> objects; // 偵測到的物件列表
// private int timestamp;

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

// public boolean isDanger() {
// return danger;
// }

// public void setDanger(boolean danger) {
// this.danger = danger;
// }

// public int getPersonCount() {
// return personCount;
// }

// public void setPersonCount(int personCount) {
// this.personCount = personCount;
// }

// public List<List<Integer>> getDangerZone() {
// return dangerZone;
// }

// public void setDangerZone(List<List<Integer>> dangerZone) {
// this.dangerZone = dangerZone;
// }

// public List<DetectedObjectDTO> getObjects() {
// return objects;
// }

// public void setObjects(List<DetectedObjectDTO> objects) {
// this.objects = objects;
// }

// }
