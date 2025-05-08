package com.example.FlightBooking.DTOs.Response.Airline;

import lombok.Data;

@Data
public class AllPlanesResponse {
    private Long id;
    private String flightNumber;
    private Long airlineId;
}
