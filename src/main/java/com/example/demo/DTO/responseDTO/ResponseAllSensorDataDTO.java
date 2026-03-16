package com.example.demo.DTO.responseDTO;

import java.util.*;

public record ResponseAllSensorDataDTO(Collection<ResponseTemperatureAndHumidityDTO> temp,
        Collection<ResponseCircuitDTO> cir,
        Collection<ResponseAirQualityDTO> aq,
        Collection<ResponseAirParticulatesDTO> ap, Collection<ResponseCamDTO> cam, ResponseAllDeviceStateDTO sta) {
}