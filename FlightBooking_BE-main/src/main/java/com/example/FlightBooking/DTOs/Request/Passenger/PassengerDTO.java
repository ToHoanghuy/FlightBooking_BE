package com.example.FlightBooking.DTOs.Request.Passenger;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PassengerDTO {
    private String fullName;
    private String email;
    private String personalId;
    private String seatNumber;
}
