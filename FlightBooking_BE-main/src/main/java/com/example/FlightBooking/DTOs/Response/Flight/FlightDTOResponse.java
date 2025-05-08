package com.example.FlightBooking.DTOs.Response.Flight;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Data
public class FlightDTOResponse {
    @NotNull (message = "Flight id")
    private Long id;
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

    @NotNull(message = "Airline ID is required")
    private Long airlineId;

    @NotNull(message = "Airline name is required")
    private String airlineName;
    private Double economyPrice;
    private Double businessPrice;
    private Double firstClassPrice;
}
