package com.example.demo.DTO.responseDTO;

import java.util.Map;

public record ResponseAllFilterLoadDTO(Map<String, Integer> threadStats, int queueSize, int lastestProcessTime) {

}
