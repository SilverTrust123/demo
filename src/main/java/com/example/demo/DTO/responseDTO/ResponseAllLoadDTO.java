package com.example.demo.DTO.responseDTO;

import java.util.Map;

public record ResponseAllLoadDTO(Map<String, Integer> threadStats, int queueSize) {

}
