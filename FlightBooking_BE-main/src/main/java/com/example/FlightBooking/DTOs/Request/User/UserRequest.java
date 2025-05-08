package com.example.FlightBooking.DTOs.Request.User;

import java.sql.Timestamp;
import java.time.LocalDate;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UserRequest {
    private String fullName;
    private String phoneNumber;
    private String address;
    private Timestamp dayOfBirth;
}
