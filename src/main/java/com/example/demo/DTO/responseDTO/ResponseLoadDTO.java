package com.example.demo.DTO.responseDTO;

import java.util.Map;

public record ResponseLoadDTO(Map<String, Integer> threadStats) {
}