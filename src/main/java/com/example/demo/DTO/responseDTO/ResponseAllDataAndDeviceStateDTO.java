package com.example.demo.DTO.responseDTO;

import java.util.Collection;

import com.example.demo.DTO.responseDTO.PLCResponseDTO.ResponseAllDPointStateDTO;
import com.example.demo.DTO.responseDTO.PLCResponseDTO.ResponseAllMPointStateDTO;

public record ResponseAllDataAndDeviceStateDTO(Collection<ResponseTemperatureAndHumidityDTO> temp,
                Collection<ResponseCircuitDTO> cir,
                Collection<ResponseAirQualityDTO> aq,
                Collection<ResponseAirParticulatesDTO> ap, Collection<ResponseCamDTO> cam, ResponseAllDPointStateDTO d,
                ResponseAllMPointStateDTO m, ResponseAllDeviceStateDTO dd) {
}
