package com.example.FlightBooking.DTOs.Response.Ticket;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class TicketResponse {
    private String airlineLogo;
    private String departAirport;
    private String arrivalAirport;
    private Timestamp departDate;
    private Timestamp arrivalDate;
    private String iataCodeDepart;
    private String iataCodeArrival;
    private List<String> seatNumber;
    private Long bookingId;
}
