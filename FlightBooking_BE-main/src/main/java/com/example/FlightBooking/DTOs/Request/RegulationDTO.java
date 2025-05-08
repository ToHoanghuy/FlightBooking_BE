package com.example.FlightBooking.DTOs.Request;

import com.example.FlightBooking.DTOs.Request.AirlineAndAirport.AirlineDTO;
import lombok.Data;

@Data
public class RegulationDTO {
    private Long id;
    private double firstClassPrice;
    private double businessPrice;
    private double economyPrice;
    private AirlineDTO airline;
}
