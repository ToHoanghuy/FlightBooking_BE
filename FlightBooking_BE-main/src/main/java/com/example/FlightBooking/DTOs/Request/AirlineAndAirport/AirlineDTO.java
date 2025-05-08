package com.example.FlightBooking.DTOs.Request.AirlineAndAirport;

import com.example.FlightBooking.DTOs.Request.RegulationDTO;
import lombok.Data;

import java.util.List;

@Data
public class AirlineDTO {
    private Long id;
    private String airlineName;
    private String logoUrl;
    private List<String> promoForAirline;
    private List<PlaneDTO> planes;
}
