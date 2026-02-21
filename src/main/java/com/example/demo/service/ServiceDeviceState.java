package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.example.demo.DTO.responseDTO.*;

@Service
public class ServiceDeviceState {

    private Map<String, Long> heartbeatMap = new ConcurrentHashMap<>();
    private Map<String, Boolean> stateMap = new ConcurrentHashMap<>() {
    };
    @Value("${offline_time}")
    private int OFFLINE_TIME;

    public void updateHeartbeat(String deviceId) {
        long currentTime = System.currentTimeMillis(); // 取得現在時間
        heartbeatMap.put(deviceId, currentTime); // 寫入點名冊
        stateMap.put(deviceId, true);
    }

    public void markOfflineIfTimeout() {
        long now = System.currentTimeMillis();
        heartbeatMap.forEach((deviceId, lastSeen) -> {
            if (now - lastSeen > OFFLINE_TIME) {
                stateMap.put(deviceId, false);
            }
        });
    }

    public ResponseAllDeviceStateDTO getAllDeviceStatuses() {
        List<ResponseDeviceStateDTO> ans = new ArrayList<ResponseDeviceStateDTO>() {

        };
        stateMap.forEach((deviceId, currState) -> {
            ResponseDeviceStateDTO curr = new ResponseDeviceStateDTO(
                    deviceId,
                    currState);
            ans.add(curr);
        });
        return new ResponseAllDeviceStateDTO(ans);
    }

}
