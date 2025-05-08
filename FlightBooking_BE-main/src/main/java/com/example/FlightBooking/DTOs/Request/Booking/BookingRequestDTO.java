package com.example.FlightBooking.DTOs.Request.Booking;

import com.example.FlightBooking.DTOs.Request.Passenger.PassengerDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Data
public class BookingRequestDTO {
    private Long flightId;
    private String bookerFullName;
    private String bookerEmail;
    private String bookerPhoneNumber;
    private Long userId;
    private Timestamp bookingDate;
    private List<PassengerDTO> passengers;
}
