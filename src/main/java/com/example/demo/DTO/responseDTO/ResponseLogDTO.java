package com.example.demo.DTO.responseDTO;

public record ResponseLogDTO(Long id, String log_level, String source, String message, int timestamp) {
    // id BIGINT AUTO_INCREMENT NOT NULL,
    // log_level VARCHAR(255),
    // source VARCHAR(255),
    // message TEXT,
    // timestamp INT,
}
