package com.example.demo.DTO.requestDTO;

public record RequestSensorBorderDTO(String sensor_group, int temp_1, int temp_2, int humi_1, int humi_2, int dust,
                int qua, int pow) {

}
