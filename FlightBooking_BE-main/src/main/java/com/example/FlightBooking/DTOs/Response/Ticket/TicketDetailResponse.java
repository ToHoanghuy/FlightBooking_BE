package com.example.FlightBooking.DTOs.Response.Ticket;

import com.example.FlightBooking.DTOs.Request.Passenger.PassengerDTO;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class TicketDetailResponse {
    private Long flightId;
    private String bookerFullName;
    private String bookerPhoneNumber;
    private String bookerEmail;
    private String airlineLogoUrl;
    private String airlineName;
    private String planeNumber;
    private String departureAirportName;
    private String arrivalAirportName;
    private String iataArrivalCode;
    private String iataDepartureCode;
    private Timestamp departureDate;
    private Timestamp arrivalDate;
    private List<PassengerDTO> passengerDTOList;
}