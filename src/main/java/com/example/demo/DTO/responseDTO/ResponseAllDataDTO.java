package com.example.demo.DTO.responseDTO;

import com.example.demo.DTO.responseDTO.PLCResponseDTO.*;
import java.util.Collection;

public record ResponseAllDataDTO(Collection<ResponseTemperatureAndHumidityDTO> temp, Collection<ResponseCircuitDTO> cir,
                Collection<ResponseAirQualityDTO> aq,
                Collection<ResponseAirParticulatesDTO> ap, Collection<ResponseCamDTO> cam, ResponseAllDPointStateDTO d,
                ResponseAllMPointStateDTO m) {

}
