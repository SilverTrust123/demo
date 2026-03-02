package com.example.demo.DTO.responseDTO;

import java.util.Collection;

public record ResponseAllDataAndDeviceStateWithoutPLCDTO(Collection<ResponseTemperatureAndHumidityDTO> temp,
        Collection<ResponseCircuitDTO> cir,
        Collection<ResponseAirQualityDTO> aq,
        Collection<ResponseAirParticulatesDTO> ap, Collection<ResponseCamDTO> cam, ResponseAllDeviceStateDTO dd) {
}