USE ICMS;
-- 溫溼度表
CREATE TABLE temperature_and_humidity (
    device_id VARCHAR(255) NOT NULL,
    temperature FLOAT,
    humidity FLOAT,
    timestamp INT,
    PRIMARY KEY (device_id, timestamp)
);

-- 電路表
CREATE TABLE circuit (
    device_id VARCHAR(255) NOT NULL,
    voltage FLOAT,
    current FLOAT,
    power FLOAT,
    energy FLOAT,
    timestamp INT,
    PRIMARY KEY (device_id, timestamp)
);

-- 空氣品質表
CREATE TABLE air_quality (
    device_id VARCHAR(255) NOT NULL,
    air_pollution FLOAT,
    timestamp INT,
    PRIMARY KEY (device_id, timestamp)
);

-- 空氣微粒表
CREATE TABLE air_particulates (
    device_id VARCHAR(255) NOT NULL,
    pm2_5 FLOAT,
    timestamp INT,
    PRIMARY KEY (device_id, timestamp)
);

--Log
CREATE TABLE log (
    id BIGINT AUTO_INCREMENT NOT NULL,
    log_level VARCHAR(255),
    source VARCHAR(255),
    message TEXT,
    timestamp INT,
    PRIMARY KEY (id)
);

--帳密
USE ICMS;
CREATE TABLE `users` (
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;	

--交接事項
USE ICMS;
CREATE TABLE `todo` (
    message VARCHAR(255) NOT NULL,
    PRIMARY KEY (`message`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE ICMS;
CREATE TABLE `device_response_log` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `device_list_data` JSON NOT NULL, 
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);