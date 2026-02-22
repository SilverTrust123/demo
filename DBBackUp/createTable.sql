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
