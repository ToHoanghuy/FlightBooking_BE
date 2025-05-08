package com.example.FlightBooking.DTOs.Request.Flight;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class FlightDTORequest {
    @NotNull(message = "Flight status is required")
    private String flightStatus;
    @NotNull(message = "Departure date is required")
    private Timestamp departureDate;
    @NotNull(message = "Arrival date is required")
    private Timestamp arrivalDate;
    @NotNull(message = "Duration is required")
    private Long duration;
    @NotNull(message = "Departure airport ID is required")
    private Long departureAirportId;
    @NotNull(message = "Arrival airport ID is required")
    private Long arrivalAirportId;
    @NotNull(message = "Plane ID is required")
    private Long planeId;
}
