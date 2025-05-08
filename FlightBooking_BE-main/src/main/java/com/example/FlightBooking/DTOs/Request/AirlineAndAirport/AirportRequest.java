package com.example.FlightBooking.DTOs.Request.AirlineAndAirport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AirportRequest {
    private String airportName;
    private String city;
    private String iataCode;
}
